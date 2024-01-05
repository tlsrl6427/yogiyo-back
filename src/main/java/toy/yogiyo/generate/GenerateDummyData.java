/*
package toy.yogiyo.generate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.address.domain.AddressType;
import toy.yogiyo.core.address.domain.MemberAddress;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.order.domain.*;
import toy.yogiyo.core.order.dto.OrderCreateRequest;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.review.domain.ReviewImage;
import toy.yogiyo.core.category.domain.Category;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.domain.SignatureMenu;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.OptionGroupLinkMenu;
import toy.yogiyo.core.menuoption.domain.OptionType;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.shop.domain.BusinessHours;
import toy.yogiyo.core.shop.domain.Days;
import toy.yogiyo.core.shop.domain.DeliveryPriceInfo;
import toy.yogiyo.core.shop.domain.Shop;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class GenerateDummyData {

    private final Generator generator;

    @PostConstruct
    public void init() {
        generator.generate();
    }

    public static class Generator {

        private final EntityManager em;
        private final JdbcTemplate jdbcTemplate;

        private final Random random = new Random();
        private final int MEMBER_N = 5000;

        private final int SHOP_N = 10000;
        private final int DELIVERYPRICE_N = 1, DELIVERYPRICE_RANDOM = 3;

        private final int MENU_N = 5, MENU_RANDOM = 5;
        private final int MENUGROUP_N = 1, MENUGROUP_RANDOM = 3;

        private final int OPTIONGROUP_N = 2, OPTIONGROUP_RANDOM = 3;
        private final int OPTION_N = 4, OPTION_RANDOM = 3;

        private final int SIGNATUREMENU_MAX = 5;

        private final int ORDER_N = 1, ORDER_RANDOM = 2;

        public Generator(EntityManager em, JdbcTemplate jdbcTemplate) {
            this.em = em;
            this.jdbcTemplate = jdbcTemplate;
        }

        @Transactional
        public void generate() {
            log.info("더미데이터 생성 시작");

            log.info("카테고리 생성");     List<Category> categories = generateCategory();
            log.info("멤버 생성");        List<Member> members = generateMember();
            log.info("점주 생성");        List<Owner> owners = generateOwner();
            log.info("가게 생성");        List<Shop> shops = generateShop(owners, categories);
            log.info("메뉴 생성");        Map<Shop, List<MenuGroup>> menuGroups = generateMenus(shops);
            log.info("옵션 생성");        Map<Shop, List<MenuOptionGroup>> optionGroups = generateOptions(shops);
            log.info("옵션 메뉴 연결");    List<OptionGroupLinkMenu> optionMenuLink = generateOptionMenuLink(shops, menuGroups, optionGroups);
            log.info("대표 메뉴 생성");    List<SignatureMenu> signatureMenus = generateSignatureMenu(shops, menuGroups);
            log.info("주문 생성");        List<Order> orders = generateOrder(shops, members, menuGroups);
            log.info("리뷰 생성");        List<Review> reviews = generateReview(orders, shops, members);


            List<MemberAddress> memberAddresses = new ArrayList<>();
            members.forEach(member -> memberAddresses.addAll(member.getMemberAddresses()));

            List<BusinessHours> businessHours = new ArrayList<>();
            List<DeliveryPriceInfo> deliveryPriceInfos = new ArrayList<>();
            List<CategoryShop> categoryShops = new ArrayList<>();
            shops.forEach(shop -> {
                businessHours.addAll(shop.getBusinessHours());
                deliveryPriceInfos.addAll(shop.getDeliveryPriceInfos());
                categoryShops.addAll(shop.getCategoryShop());
            });

            List<MenuGroup> menuGroupsList = new ArrayList<>();
            menuGroups.forEach((shop, menuGroup) -> {
                menuGroupsList.addAll(menuGroup);
            });
            List<Menu> menus = new ArrayList<>();
            menuGroupsList.forEach(menuGroup -> {
                menus.addAll(menuGroup.getMenus());
            });

            List<MenuOptionGroup> menuOptionGroupList = new ArrayList<>();
            optionGroups.forEach((shop, optionGroup) -> {
                menuOptionGroupList.addAll(optionGroup);
            });
            List<MenuOption> menuOptions = new ArrayList<>();
            menuOptionGroupList.forEach(optionGroup -> {
                menuOptions.addAll(optionGroup.getMenuOptions());
            });

            List<OrderItem> orderItems = new ArrayList<>();
            orders.forEach(order -> orderItems.addAll(order.getOrderItems()));

            List<ReviewImage> reviewImages = new ArrayList<>();
//            reviews.forEach(review -> reviewImages.addAll(review.getReviewImages()));


            log.info("카테고리 insert");
            jdbcTemplate.batchUpdate("insert into category (name) values (?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, categories.get(i).getName());
                        }

                        @Override
                        public int getBatchSize() {
                            return categories.size();
                        }
                    }
            );

            log.info("맴버 insert");
            jdbcTemplate.batchUpdate("insert into members (created_at, updated_at, email, nickname, password, provider_type) values (?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            LocalDateTime now = LocalDateTime.now();
                            ps.setTimestamp(1, Timestamp.valueOf(now));
                            ps.setTimestamp(2, Timestamp.valueOf(now));
                            ps.setString(3, members.get(i).getEmail());
                            ps.setString(4, members.get(i).getNickname());
                            ps.setString(5, members.get(i).getPassword());
                            ps.setString(6, members.get(i).getProviderType().name());
                        }

                        @Override
                        public int getBatchSize() {
                            return members.size();
                        }
                    }
            );

            log.info("멤버 주소 insert");
            jdbcTemplate.batchUpdate("insert into member_address (detail, street, zipcode, address_type, latitude, longitude, member_id, nickname) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, memberAddresses.get(i).getAddress().getDetail());
                            ps.setString(2, memberAddresses.get(i).getAddress().getStreet());
                            ps.setString(4, memberAddresses.get(i).getAddressType().name());
                            ps.setDouble(5, memberAddresses.get(i).getLatitude());
                            ps.setDouble(6, memberAddresses.get(i).getLongitude());
                            ps.setLong(7, memberAddresses.get(i).getMember().getId());
                            ps.setString(8, memberAddresses.get(i).getNickname());
                        }

                        @Override
                        public int getBatchSize() {
                            return memberAddresses.size();
                        }
                    }
            );

            log.info("점주 insert");
            jdbcTemplate.batchUpdate("insert into owner (created_at, updated_at, email, nickname, password, provider_type) values (?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            LocalDateTime now = LocalDateTime.now();
                            ps.setTimestamp(1, Timestamp.valueOf(now));
                            ps.setTimestamp(2, Timestamp.valueOf(now));
                            ps.setString(3, owners.get(i).getEmail());
                            ps.setString(4, owners.get(i).getNickname());
                            ps.setString(5, owners.get(i).getPassword());
                            ps.setString(6, owners.get(i).getProviderType().name());
                        }

                        @Override
                        public int getBatchSize() {
                            return owners.size();
                        }
                    }
            );

            log.info("가게 insert");
            jdbcTemplate.batchUpdate("insert into shop (created_at, updated_at, address, banner, call_number, delivery_score, delivery_time, icon, latitude, longitude, min_delivery_price, min_order_price, name, notice_images, notice_title, order_num, owner_id, owner_notice, owner_reply_num, quantity_score, review_num, taste_score, total_score, like_num) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            LocalDateTime now = LocalDateTime.now();
                            ps.setTimestamp(1, Timestamp.valueOf(now));
                            ps.setTimestamp(2, Timestamp.valueOf(now));
                            ps.setString(3, shops.get(i).getAddress());
                            ps.setString(4, shops.get(i).getBanner());
                            ps.setString(5, shops.get(i).getCallNumber());
                            ps.setDouble(6, shops.get(i).getDeliveryScore());
                            ps.setInt(7, shops.get(i).getDeliveryTime());
                            ps.setString(8, shops.get(i).getIcon());
                            ps.setDouble(9, shops.get(i).getLatitude());
                            ps.setDouble(10, shops.get(i).getLongitude());
                            ps.setInt(11, shops.get(i).getMinDeliveryPrice());
                            ps.setInt(12, shops.get(i).getMinOrderPrice());
                            ps.setString(13, shops.get(i).getName());
                            ps.setString(14, String.join(",", shops.get(i).getNoticeImages()));
                            ps.setString(15, shops.get(i).getNoticeTitle());
                            ps.setLong(16, shops.get(i).getOrderNum());
                            ps.setLong(17, shops.get(i).getOwner().getId());
                            ps.setString(18, shops.get(i).getOwnerNotice());
                            ps.setLong(19, shops.get(i).getOwnerReplyNum());
                            ps.setDouble(20, shops.get(i).getQuantityScore());
                            ps.setLong(21, shops.get(i).getReviewNum());
                            ps.setDouble(22, shops.get(i).getTasteScore());
                            ps.setDouble(23, shops.get(i).getTotalScore());
                            ps.setLong(24, shops.get(i).getLikeNum());
                        }

                        @Override
                        public int getBatchSize() {
                            return shops.size();
                        }
                    }
            );

            log.info("영업 시간 insert");
            jdbcTemplate.batchUpdate("insert into business_hours (break_time_end, break_time_start, close_time, day_of_week, is_open, open_time, shop_id) values (?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setTime(1, businessHours.get(i).getBreakTimeEnd() == null ? null : Time.valueOf(businessHours.get(i).getBreakTimeEnd()));
                            ps.setTime(2, businessHours.get(i).getBreakTimeStart() == null ? null : Time.valueOf(businessHours.get(i).getBreakTimeStart()));
                            ps.setTime(3, Time.valueOf(businessHours.get(i).getCloseTime()));
                            ps.setInt(4, businessHours.get(i).getDayOfWeek().ordinal());
                            ps.setBoolean(5, businessHours.get(i).isOpen());
                            ps.setTime(6, Time.valueOf(businessHours.get(i).getOpenTime()));
                            ps.setLong(7, businessHours.get(i).getShop().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return businessHours.size();
                        }
                    }
            );


            log.info("배달 요금 insert");
            jdbcTemplate.batchUpdate("insert into delivery_price_info (delivery_price, order_price, shop_id) values (?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, deliveryPriceInfos.get(i).getDeliveryPrice());
                            ps.setInt(2, deliveryPriceInfos.get(i).getOrderPrice());
                            ps.setLong(3, deliveryPriceInfos.get(i).getShop().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return deliveryPriceInfos.size();
                        }
                    }
            );

            log.info("카테고리 - 샵 insert");
            jdbcTemplate.batchUpdate("insert into category_shop (category_id, shop_id) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, categoryShops.get(i).getCategory().getId());
                            ps.setLong(2, categoryShops.get(i).getShop().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return categoryShops.size();
                        }
                    }
            );

            log.info("메뉴 그룹 insert");
            jdbcTemplate.batchUpdate("insert into menu_group (content, name, shop_id) values (?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, menuGroupsList.get(i).getContent());
                            ps.setString(2, menuGroupsList.get(i).getName());
                            ps.setLong(3, menuGroupsList.get(i).getShop().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return menuGroupsList.size();
                        }
                    }
            );

            log.info("메뉴 insert");
            jdbcTemplate.batchUpdate("insert into menu (content, menu_group_id, name, picture, position, price) values (?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, menus.get(i).getContent());
                            ps.setLong(2, menus.get(i).getMenuGroup().getId());
                            ps.setString(3, menus.get(i).getName());
                            ps.setString(4, menus.get(i).getPicture());
                            ps.setInt(5, menus.get(i).getPosition());
                            ps.setInt(6, menus.get(i).getPrice());
                        }

                        @Override
                        public int getBatchSize() {
                            return menus.size();
                        }
                    }
            );

            log.info("메뉴 옵션 그룹 insert");
            jdbcTemplate.batchUpdate("insert into menu_option_group (count, is_possible_count, name, option_type, position, shop_id) values (?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, menuOptionGroupList.get(i).getCount());
                            ps.setBoolean(2, menuOptionGroupList.get(i).getIsPossibleCount());
                            ps.setString(3, menuOptionGroupList.get(i).getName());
                            ps.setString(4, menuOptionGroupList.get(i).getOptionType().name());
                            ps.setInt(5, menuOptionGroupList.get(i).getPosition());
                            ps.setLong(6, menuOptionGroupList.get(i).getShop().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return menuOptionGroupList.size();
                        }
                    }
            );

            log.info("메뉴 옵션 insert");
            jdbcTemplate.batchUpdate("insert into menu_option (content, menu_option_group_id, position, price) values (?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, menuOptions.get(i).getContent());
                            ps.setLong(2, menuOptions.get(i).getMenuOptionGroup().getId());
                            ps.setInt(3, menuOptions.get(i).getPosition());
                            ps.setInt(4, menuOptions.get(i).getPrice());
                        }

                        @Override
                        public int getBatchSize() {
                            return menuOptions.size();
                        }
                    }
            );

            log.info("메뉴 <-(link)-> 옵션 그룹 insert");
            jdbcTemplate.batchUpdate("insert into option_group_link_menu (menu_id, menu_option_group_id) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, optionMenuLink.get(i).getMenu().getId());
                            ps.setLong(2, optionMenuLink.get(i).getMenuOptionGroup().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return optionMenuLink.size();
                        }
                    }
            );

            log.info("대표 메뉴 insert");
            jdbcTemplate.batchUpdate("insert into signature_menu (menu_id, position, shop_id) values (?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, signatureMenus.get(i).getMenu().getId());
                            ps.setInt(2, signatureMenus.get(i).getPosition());
                            ps.setLong(3, signatureMenus.get(i).getShop().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return signatureMenus.size();
                        }
                    }
            );


            log.info("주문 insert");
            jdbcTemplate.batchUpdate("insert into orders (created_at, updated_at, detail, street, zipcode, delivery_price, member_id, order_number, order_type, payment_type, phone_number, request_door, request_msg, request_spoon, shop_id, status, total_payment_price, total_price) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            LocalDateTime now = LocalDateTime.now();
                            ps.setTimestamp(1, Timestamp.valueOf(now));
                            ps.setTimestamp(2, Timestamp.valueOf(now));
                            ps.setString(3, orders.get(i).getAddress().getDetail());
                            ps.setString(4, orders.get(i).getAddress().getStreet());
                            ps.setInt(6, orders.get(i).getDeliveryPrice());
                            ps.setLong(7, orders.get(i).getMember().getId());
                            ps.setString(8, orders.get(i).getOrderNumber());
                            ps.setString(9, orders.get(i).getOrderType().name());
                            ps.setString(10, orders.get(i).getPaymentType().name());
                            ps.setString(11, orders.get(i).getPhoneNumber());
                            ps.setBoolean(12, orders.get(i).isRequestDoor());
                            ps.setString(13, orders.get(i).getRequestMsg());
                            ps.setBoolean(14, orders.get(i).isRequestSpoon());
                            ps.setLong(15, orders.get(i).getShop().getId());
                            ps.setInt(16, orders.get(i).getStatus().ordinal());
                            ps.setInt(17, orders.get(i).getTotalPaymentPrice());
                            ps.setInt(18, orders.get(i).getTotalPrice());
                        }

                        @Override
                        public int getBatchSize() {
                            return orders.size();
                        }
                    }
            );

            log.info("주문 아이템 insert");
            jdbcTemplate.batchUpdate("insert into order_item (created_at, updated_at, menu_name, order_id, price, quantity) values (?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            LocalDateTime now = LocalDateTime.now();
                            ps.setTimestamp(1, Timestamp.valueOf(now));
                            ps.setTimestamp(2, Timestamp.valueOf(now));
                            ps.setString(3, orderItems.get(i).getMenuName());
                            ps.setLong(4, orderItems.get(i).getOrder().getId());
                            ps.setInt(5, orderItems.get(i).getPrice());
                            ps.setInt(6, orderItems.get(i).getQuantity());
                        }

                        @Override
                        public int getBatchSize() {
                            return orderItems.size();
                        }
                    }
            );

            log.info("리뷰 insert");
            jdbcTemplate.batchUpdate("insert into review (created_at, updated_at, content, delivery_score, owner_reply, owner_reply_created_at, quantity_score, shop_id, shop_name, taste_score, total_score, member_id, order_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            LocalDateTime now = LocalDateTime.now();
                            ps.setTimestamp(1, Timestamp.valueOf(now));
                            ps.setTimestamp(2, Timestamp.valueOf(now));
                            ps.setString(3, reviews.get(i).getContent());
                            ps.setDouble(4, reviews.get(i).getDeliveryScore());
                            ps.setString(5, reviews.get(i).getOwnerReply());
                            ps.setTimestamp(6, reviews.get(i).getOwnerReplyCreatedAt() == null ? null : Timestamp.valueOf(reviews.get(i).getOwnerReplyCreatedAt()));
                            ps.setDouble(7, reviews.get(i).getQuantityScore());
                            ps.setLong(8, reviews.get(i).getShopId());
                            ps.setString(9, reviews.get(i).getShopName());
                            ps.setDouble(10, reviews.get(i).getTasteScore());
                            ps.setDouble(11, reviews.get(i).getTotalScore());
                            ps.setLong(12, reviews.get(i).getMember().getId());
                            ps.setLong(13, reviews.get(i).getOrder().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return reviews.size();
                        }
                    }
            );

            log.info("리뷰 이미지 insert");
            jdbcTemplate.batchUpdate("insert into review_image (img_src, review_id) values (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, reviewImages.get(i).getImgSrc());
                            ps.setLong(2, reviewImages.get(i).getReview().getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return reviewImages.size();
                        }
                    }
            );

            log.info("더미데이터 생성 종료");
        }

        private List<Category> generateCategory() {
            List<Category> categories = List.of(
                    Category.builder().id(1L).name("치킨").build(),
                    Category.builder().id(2L).name("한식").build(),
                    Category.builder().id(3L).name("중국집").build(),
                    Category.builder().id(4L).name("버거").build(),
                    Category.builder().id(5L).name("피자/양식").build(),
                    Category.builder().id(6L).name("분식").build(),
                    Category.builder().id(7L).name("족발/보쌈").build(),
                    Category.builder().id(8L).name("카페/디저트").build(),
                    Category.builder().id(9L).name("일식/돈까스").build(),
                    Category.builder().id(10L).name("찜/탕").build(),
                    Category.builder().id(11L).name("회/초밥").build(),
                    Category.builder().id(12L).name("고기/구이").build(),
                    Category.builder().id(13L).name("야식").build(),
                    Category.builder().id(14L).name("아시안").build(),
                    Category.builder().id(15L).name("샌드위치").build(),
                    Category.builder().id(16L).name("샐러드").build(),
                    Category.builder().id(17L).name("도시락/죽").build(),
                    Category.builder().id(18L).name("프랜차이즈").build(),
                    Category.builder().id(19L).name("1인분주문").build(),
                    Category.builder().id(20L).name("신규맛집").build()
            );

            return categories;
        }

        private List<Member> generateMember() {
            List<Member> members = new ArrayList<>();
            Long ai = getAutoIncrement("members");

            for (long i = ai; i < MEMBER_N + ai; i++) {
                Member member = Member.builder()
                        .id(i)
                        .nickname("사용자 " + i)
                        .email("member" + i + "@test.com")
                        .providerType(ProviderType.DEFAULT)
                        .memberAddresses(new ArrayList<>())
                        .password("1234")
                        .build();

                member.addMemberAddresses(
                        MemberAddress.builder()
                                .member(member)
                                .address(new Address("서울 송파구 올림픽로 300", null))
                                .addressType(AddressType.ELSE)
                                // 롯데월드타워 기준으로 분포  (37.512460, 127.102546) | 5km 떨어진 지점 (37.530078, 127.155148) | 차이 (0.017618, 0.052602)
                                .latitude(37.512460 + random.nextDouble() * 0.1)
                                .longitude(127.102546 + random.nextDouble() * 0.1)
                                .build());

                members.add(member);
            }

            return members;
        }

        private List<Owner> generateOwner() {
            List<Owner> owners = new ArrayList<>();
            long ai = getAutoIncrement("owner");

            for (long i = ai; i < SHOP_N + ai; i++) {
                Owner owner = Owner.builder()
                        .id(i)
                        .nickname("점주 " + i)
                        .email("owner" + i + "@test.com")
                        .providerType(ProviderType.DEFAULT)
                        .build();
                owners.add(owner);
            }

            return owners;
        }

        private List<Shop> generateShop(List<Owner> owners, List<Category> categories) {
            List<Shop> shops = new ArrayList<>();
            long ai = getAutoIncrement("shop");

            for (long i = ai; i < SHOP_N + ai; i++) {
                double quantityScore = 5 * random.nextDouble();
                double tasteScore = 5 * random.nextDouble();
                double deliveryScore = 5 * random.nextDouble();

                Shop shop = Shop.builder()
                        .id(i)
                        .name("음식점 " + i)
                        .address("서울특별시 송파구 올림픽로 300")
                        .callNumber("010-1234-5678")
                        .owner(owners.get((int)(i - ai)))
                        .ownerNotice("공지사항 입니다.")
                        .noticeTitle("공지사항")
                        .deliveryScore(5 * random.nextDouble())
                        .deliveryTime(30 + random.nextInt(30))
                        // 롯데월드타워 기준으로 분포  (37.512460, 127.102546) | 5km 떨어진 지점 (37.530078, 127.155148) | 차이 (0.017618, 0.052602)
                        .latitude(37.512460 + random.nextDouble() * 0.1)
                        .longitude(127.102546 + random.nextDouble() * 0.1)
                        .reviewNum(random.nextInt(500))
                        .likeNum(random.nextInt(500))
                        .orderNum(random.nextInt(500))
                        .quantityScore(quantityScore)
                        .tasteScore(tasteScore)
                        .deliveryScore(deliveryScore)
                        .totalScore((quantityScore+tasteScore+deliveryScore)/3)
                        .icon(getFilePath("yogiyo-logo.jpg"))
                        .banner(getFilePath("banner.jpg"))
                        .build();

                shop.updateBusinessHours(generateBusinessHours());
                shop.changeDeliveryPrices(generateDeliveryPrices());

                CategoryShop categoryShop = CategoryShop.builder()
                        .category(categories.get(random.nextInt(categories.size())))
                        .shop(shop)
                        .build();
                shop.getCategoryShop().add(categoryShop);

                shops.add(shop);
            }

            return shops;
        }

        private List<BusinessHours> generateBusinessHours() {
            List<BusinessHours> businessHours = new ArrayList<>();
            for (int j = 1; j <= 7; j++) {
                int hours = 18 + random.nextInt(9);
                BusinessHours.BusinessHoursBuilder builder = BusinessHours.builder()
                        .isOpen(random.nextBoolean())
                        .openTime(LocalTime.of(7 + random.nextInt(3), 0))
                        .closeTime(LocalTime.of(hours > 23 ? hours - 24 : hours, 0))
                        .dayOfWeek(Days.values()[j]);

                if (random.nextBoolean()) {
                    builder.breakTimeStart(LocalTime.of(14, 0));
                    builder.breakTimeEnd(LocalTime.of(16, 0));
                }

                businessHours.add(builder.build());
            }
            return businessHours;
        }

        private List<DeliveryPriceInfo> generateDeliveryPrices() {
            List<DeliveryPriceInfo> deliveryPriceInfos = new ArrayList<>();
            int n = DELIVERYPRICE_N + random.nextInt(DELIVERYPRICE_RANDOM);
            for (int i = 0; i < n; i++) {
                deliveryPriceInfos.add(new DeliveryPriceInfo(10000 + random.nextInt(40) * 1000, random.nextInt(10)*500));
            }

            return deliveryPriceInfos;
        }

        private Map<Shop, List<MenuOptionGroup>> generateOptions(List<Shop> shops) {
            Map<Shop, List<MenuOptionGroup>> optionGroups = new HashMap<>();

            long optionGroupId = getAutoIncrement("menu_option_group");
            long optionId = getAutoIncrement("menu_option");

            for (Shop shop : shops) {
                List<MenuOptionGroup> list = new ArrayList<>();
                optionGroups.put(shop, list);

                int n = OPTIONGROUP_N + random.nextInt(OPTIONGROUP_RANDOM);
                for (int i = 1; i <= n; i++) {
                    int count = random.nextInt(3);
                    MenuOptionGroup optionGroup = MenuOptionGroup.builder()
                            .id(optionGroupId++)
                            .name("옵션그룹 " + i)
                            .shop(shop)
                            .count(count)
                            .isPossibleCount(count == 0)
                            .optionType(random.nextBoolean() ? OptionType.OPTIONAL : OptionType.REQUIRED)
                            .position(i)
                            .build();

                    int m = OPTION_N + random.nextInt(OPTION_RANDOM);
                    for (int j = 1; j <= m; j++) {
                        MenuOption menuOption = MenuOption.builder()
                                .id(optionId++)
                                .content("옵션 " + j)
                                .price((1 + random.nextInt(6)) * 500)
                                .position(j)
                                .menuOptionGroup(optionGroup)
                                .build();

                        optionGroup.getMenuOptions().add(menuOption);
                    }
                    list.add(optionGroup);
                }
            }

            return optionGroups;
        }

        private Map<Shop, List<MenuGroup>> generateMenus(List<Shop> shops) {
            Map<Shop, List<MenuGroup>> menuGroups = new HashMap<>();
            long menuGroupId = getAutoIncrement("menu_group");
            long menuId = getAutoIncrement("menu");

            for (Shop shop : shops) {
                int n = MENUGROUP_N + random.nextInt(MENUGROUP_RANDOM);
                List<MenuGroup> list = new ArrayList<>();
                menuGroups.put(shop, list);

                for (int i = 1; i <= n; i++) {
                    MenuGroup menuGroup = MenuGroup.builder()
                            .id(menuGroupId++)
                            .name("메뉴 그룹 " + i)
                            .shop(shop)
                            .content("메뉴 그룹 " + i + " 입니다.")
                            .build();

                    int m = MENU_N + random.nextInt(MENU_RANDOM);
                    for (int j = 1; j <= m; j++) {
                        Menu menu = Menu.builder()
                                .id(menuId++)
                                .name("메뉴 " + j)
                                .content("메뉴 " + j + " 입니다.")
                                .picture(getFilePath("hamburger.jpg"))
                                .price((1 + random.nextInt(20)) * 1000)
                                .menuGroup(menuGroup)
                                .position(j)
                                .build();
                        menuGroup.getMenus().add(menu);
                    }

                    list.add(menuGroup);
                }
            }

            return menuGroups;
        }

        private List<OptionGroupLinkMenu> generateOptionMenuLink(List<Shop> shops, Map<Shop, List<MenuGroup>> menuGroups, Map<Shop, List<MenuOptionGroup>> optionGroups) {
            List<OptionGroupLinkMenu> linkMenus = new ArrayList<>();

            long linkMenuId = getAutoIncrement("option_group_link_menu");

            int shopSize = shops.size();
            for (int i = 0; i < shopSize; i++) {
                Shop shop = shops.get(i);

                List<MenuGroup> mg = menuGroups.get(shop);
                List<MenuOptionGroup> og = optionGroups.get(shop);

                for (MenuGroup menuGroup : mg) {
                    for (Menu menu : menuGroup.getMenus()) {
                        OptionGroupLinkMenu linkMenu = OptionGroupLinkMenu.builder()
                                .id(linkMenuId++)
                                .menu(menu)
                                .menuOptionGroup(og.get(random.nextInt(og.size())))
                                .build();
                        linkMenus.add(linkMenu);
                    }
                }
            }

            return linkMenus;
        }

        private List<SignatureMenu> generateSignatureMenu(List<Shop> shops, Map<Shop, List<MenuGroup>> menuGroups) {
            List<SignatureMenu> signatureMenus = new ArrayList<>();

            long signatureMenuId = getAutoIncrement("signature_menu");

            for (Shop shop : shops) {
                List<MenuGroup> mg = menuGroups.get(shop);
                for (MenuGroup menuGroup : mg) {
                    for (int i = 0; i < Math.min(menuGroup.getMenus().size(), SIGNATUREMENU_MAX); i++) {
                        Menu menu = menuGroup.getMenus().get(i);
                        SignatureMenu signatureMenu = SignatureMenu.builder()
                                .id(signatureMenuId++)
                                .shop(shop)
                                .menu(menu)
                                .position(i + 1)
                                .build();

                        signatureMenus.add(signatureMenu);
                    }
                }
            }

            return signatureMenus;
        }

        private List<Order> generateOrder(List<Shop> shops, List<Member> members, Map<Shop, List<MenuGroup>> menuGroups) {
            List<Order> orders = new ArrayList<>();

            long orderId = getAutoIncrement("orders");
            long orderItemId = getAutoIncrement("order_item");

            for (Member member : members) {
                int n = ORDER_N + random.nextInt(ORDER_RANDOM);
                for (int i = 0; i < n; i++) {
                    Shop shop = shops.get(random.nextInt(shops.size()));

                    List<Menu> menus = new ArrayList<>();
                    menuGroups.get(shop)
                            .forEach(menuGroup -> menus.addAll(menuGroup.getMenus()));

                    List<OrderItem> orderItems = new ArrayList<>();
                    int m = 1+random.nextInt(3);
                    for (int j = 0; j < m; j++) {
                        Menu menu = menus.get(random.nextInt(menus.size()));

                        OrderItem orderItem = OrderItem.builder()
                                .id(orderItemId++)
                                .menuName(menu.getName())
                                .quantity(1 + random.nextInt(3))
                                .orderItemOptions(List.of())
                                .price(10000)
                                .build();
                        orderItems.add(orderItem);
                    }


                    int deliveryPrice = shop.getDeliveryPriceInfos().get(0).getDeliveryPrice();
                    OrderCreateRequest request = OrderCreateRequest.builder()
                            .orderType(OrderType.DELIVERY)
                            .deliveryPrice(deliveryPrice)
                            .requestSpoon(random.nextBoolean())
                            .requestDoor(random.nextBoolean())
                            .requestMsg("안전하게 배달해주세요 :)")
                            .paymentType(PaymentType.CARD)
                            .address(member.getMemberAddresses().get(0).getAddress())
                            .shopId(shop.getId())
                            .orderItems(orderItems)
                            .totalPaymentPrice(20000)
                            .deliveryPrice(deliveryPrice)
                            .totalPrice(20000-deliveryPrice)
                            .build();

                    Order order = Order.createOrder(member, shop, request);
                    order.setId(orderId++);
                    orders.add(order);
                }
            }

            return orders;
        }

        private List<Review> generateReview(List<Order> orders, List<Shop> shops, List<Member> members) {
            List<Review> reviews = new ArrayList<>();

            long reviewId = getAutoIncrement("review");

            for (Order order : orders) {
                if (random.nextBoolean()) continue;

                Shop s = shops.stream()
                        .filter(shop -> order.getShop() == shop)
                        .findFirst().get();

                double quantityScore = 5 * random.nextDouble();
                double deliveryScore = 5 * random.nextDouble();
                double tasteScore = 5 * random.nextDouble();

                Review.ReviewBuilder reviewBuilder = Review.builder()
                        .id(reviewId++)
                        .order(order)
                        .content("맛있어요")
                        .quantityScore(quantityScore)
                        .deliveryScore(deliveryScore)
                        .tasteScore(tasteScore)
                        .totalScore((quantityScore + deliveryScore + tasteScore) / 3)
                        .shopName(s.getName())
                        .shopId(s.getId())
                        .member(members.stream().filter(member -> order.getMember() == member).findFirst().get());

                if (random.nextBoolean()) {
                    reviewBuilder.ownerReply("감사합니다")
                            .ownerReplyCreatedAt(LocalDateTime.now());
                }

                Review review = reviewBuilder.build();
                if (random.nextBoolean()) {
                    ReviewImage reviewImage = new ReviewImage(null, getFilePath("hamburger.jpg"), review);
                }

                reviews.add(review);
            }

            return reviews;
        }

        private String getFilePath(String filename) {
            return "/images/" + filename;
        }

        private Long getAutoIncrement(String tableName) {
            String sql = "select auto_increment from information_schema.tables where table_schema = ? and table_name = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, "yogiyo", tableName);
        }
    }
}
*/
