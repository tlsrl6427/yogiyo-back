package toy.yogiyo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.core.address.domain.Address;
import toy.yogiyo.core.address.domain.AddressType;
import toy.yogiyo.core.address.domain.MemberAddress;
import toy.yogiyo.core.member.domain.Member;
import toy.yogiyo.core.member.domain.ProviderType;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateDummyData {

    private final Generator generator;

    @PostConstruct
    public void init() {
        generator.generate();
    }

//    @Component
    public static class Generator {

        private final EntityManager em;
        private final Random random = new Random();
        private final int SHOP_COUNT = 1000;

        public Generator(EntityManager em) {
            this.em = em;
        }

        @Transactional
        public void generate() {
            log.info("더미데이터 생성 시작");
            List<Category> categories = generateCategory();
            List<Member> members = generateMember();
            List<Owner> owners = generateOwner();
            List<Shop> shops = generateShop(owners, categories);
            List<MenuOptionGroup> optionGroups = generateOptions(shops);
            List<MenuGroup> menuGroups = generateMenus(shops);
            List<OptionGroupLinkMenu> optionMenuLink = generateOptionMenuLink(shops, menuGroups, optionGroups);
            List<SignatureMenu> signatureMenus = generateSignatureMenu(shops, menuGroups);
            List<Order> orders = generateOrder(shops, members, menuGroups);
            List<Review> reviews = generateReview(orders, shops, members);
            log.info("더미데이터 생성 종료");
        }

        private List<Category> generateCategory() {
            List<Category> categories = List.of(
                    Category.builder().name("치킨").build(),
                    Category.builder().name("한식").build(),
                    Category.builder().name("중국집").build(),
                    Category.builder().name("버거").build(),
                    Category.builder().name("피자/양식").build(),
                    Category.builder().name("분식").build(),
                    Category.builder().name("족발/보쌈").build(),
                    Category.builder().name("카페/디저트").build(),
                    Category.builder().name("일식/돈까스").build(),
                    Category.builder().name("찜/탕").build(),
                    Category.builder().name("회/초밥").build(),
                    Category.builder().name("고기/구이").build(),
                    Category.builder().name("야식").build(),
                    Category.builder().name("아시안").build(),
                    Category.builder().name("샌드위치").build(),
                    Category.builder().name("샐러드").build(),
                    Category.builder().name("도시락/죽").build(),
                    Category.builder().name("프랜차이즈").build(),
                    Category.builder().name("1인분주문").build(),
                    Category.builder().name("신규맛집").build()
            );

            for (Category category : categories) {
                em.persist(category);
            }

            return categories;
        }

        private List<Member> generateMember() {
            List<Member> members = new ArrayList<>();

            for (int i = 1; i <= 50; i++) {
                Member member = Member.builder()
                        .nickname("사용자 " + i)
                        .email("member" + i + "@test.com")
                        .providerType(ProviderType.DEFAULT)
                        .memberAddresses(new ArrayList<>())
                        .build();

                member.addMemberAddresses(
                        MemberAddress.builder()
                                .member(member)
                                .address(new Address("05551", "서울 송파구 올림픽로 300", null))
                                .addressType(AddressType.ELSE)
                                .build());

                members.add(member);
                em.persist(member);
            }

            return members;
        }

        private List<Owner> generateOwner() {
            List<Owner> owners = new ArrayList<>();

            for (int i = 1; i <= SHOP_COUNT; i++) {
                Owner owner = Owner.builder()
                        .nickname("점주 " + i)
                        .email("owner" + i + "@test.com")
                        .providerType(ProviderType.DEFAULT)
                        .build();
                owners.add(owner);
                em.persist(owner);
            }

            return owners;
        }

        private List<Shop> generateShop(List<Owner> owners, List<Category> categories) {
            List<Shop> shops = new ArrayList<>();

            for (int i = 1; i <= SHOP_COUNT; i++) {
                Shop shop = Shop.builder()
                        .name("음식점 " + i)
                        .address("서울특별시 송파구 올림픽로 300")
                        .callNumber("010-1234-5678")
                        .owner(owners.get(i - 1))
                        .ownerNotice("공지사항 입니다.")
                        .noticeTitle("공지사항")
                        .deliveryScore(5 * random.nextDouble())
                        .deliveryTime(30 + random.nextInt(30))
                        // 롯데월드타워 기준으로 분포  (37.512460, 127.102546) | 5km 떨어진 지점 (37.530078, 127.155148) | 차이 (0.017618, 0.052602)
                        .latitude(37.512460 + random.nextDouble() * 0.1)
                        .longitude(127.102546 + random.nextDouble() * 0.1)
                        .reviewNum(random.nextInt(500))
                        .wishNum(random.nextInt(500))
                        .quantityScore(5 * random.nextDouble())
                        .tasteScore(5 * random.nextDouble())
                        .deliveryScore(5 * random.nextDouble())
                        .icon(getFilePath("yogiyo-logo.jpg"))
                        .banner(getFilePath("banner.jpg"))
                        .build();

                shop.changeBusinessHours(generateBusinessHours());
                shop.changeDeliveryPrices(generateDeliveryPrices());

                CategoryShop categoryShop = CategoryShop.builder()
                        .category(categories.get(random.nextInt(categories.size())))
                        .shop(shop)
                        .build();
                shop.getCategoryShop().add(categoryShop);

                shops.add(shop);
                em.persist(shop);
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
            int n = 1 + random.nextInt(3);
            for (int i = 0; i < n; i++) {
                deliveryPriceInfos.add(new DeliveryPriceInfo(10000 + random.nextInt(40) * 1000, random.nextInt(10)*500));
            }

            return deliveryPriceInfos;
        }

        private List<MenuOptionGroup> generateOptions(List<Shop> shops) {
            List<MenuOptionGroup> optionGroups = new ArrayList<>();

            for (Shop shop : shops) {
                int n = 2 + random.nextInt(3);
                for (int i = 1; i <= n; i++) {
                    int count = random.nextInt(3);
                    MenuOptionGroup optionGroup = MenuOptionGroup.builder()
                            .name("옵션그룹 " + i)
                            .shop(shop)
                            .count(count)
                            .isPossibleCount(count == 0)
                            .optionType(random.nextBoolean() ? OptionType.OPTIONAL : OptionType.REQUIRED)
                            .position(i)
                            .build();

                    int m = 4 + random.nextInt(3);
                    for (int j = 1; j <= m; j++) {
                        optionGroup.createMenuOption("옵션 " + j, (1+random.nextInt(6))*500);
                    }
                    optionGroups.add(optionGroup);
                    em.persist(optionGroup);
                }
            }

            return optionGroups;
        }

        private List<MenuGroup> generateMenus(List<Shop> shops) {
            List<MenuGroup> menuGroups = new ArrayList<>();

            for (Shop shop : shops) {
                int n = 1 + random.nextInt(3);
                for (int i = 1; i <= n; i++) {
                    MenuGroup menuGroup = MenuGroup.builder()
                            .name("메뉴 그룹 " + i)
                            .shop(shop)
                            .content("메뉴 그룹 " + i + " 입니다.")
                            .build();

                    int m = 5 + random.nextInt(5);
                    for (int j = 1; j <= m; j++) {
                        Menu menu = Menu.builder()
                                .name("메뉴 " + j)
                                .content("메뉴 " + j + " 입니다.")
                                .picture(getFilePath("hamburger.jpg"))
                                .price((1 + random.nextInt(20)) * 1000)
                                .menuGroup(menuGroup)
                                .position(j)
                                .build();
                        menuGroup.getMenus().add(menu);
                        em.persist(menu);
                    }

                    menuGroups.add(menuGroup);
                    em.persist(menuGroup);
                }
            }

            return menuGroups;
        }

        private List<OptionGroupLinkMenu> generateOptionMenuLink(List<Shop> shops, List<MenuGroup> menuGroups, List<MenuOptionGroup> optionGroups) {
            List<OptionGroupLinkMenu> linkMenus = new ArrayList<>();

            for (Shop shop : shops) {
                List<MenuGroup> mg = menuGroups.stream()
                        .filter(menuGroup -> menuGroup.getShop() == shop)
                        .collect(Collectors.toList());

                List<MenuOptionGroup> og = optionGroups.stream()
                        .filter(optionGroup -> optionGroup.getShop() == shop)
                        .collect(Collectors.toList());

                for (MenuGroup menuGroup : mg) {
                    for (Menu menu : menuGroup.getMenus()) {
                        OptionGroupLinkMenu linkMenu = OptionGroupLinkMenu.builder()
                                .menu(menu)
                                .menuOptionGroup(og.get(random.nextInt(og.size())))
                                .build();
                        linkMenus.add(linkMenu);
                        em.persist(linkMenu);
                    }
                }
            }

            return linkMenus;
        }

        private List<SignatureMenu> generateSignatureMenu(List<Shop> shops, List<MenuGroup> menuGroups) {
            List<SignatureMenu> signatureMenus = new ArrayList<>();

            for (Shop shop : shops) {
                menuGroups.stream()
                        .filter(menuGroup -> menuGroup.getShop() == shop)
                        .forEach(menuGroup -> {
                            for (int i = 0; i < Math.min(menuGroup.getMenus().size(), 5); i++) {
                                Menu menu = menuGroup.getMenus().get(i);
                                SignatureMenu signatureMenu = SignatureMenu.builder()
                                        .shop(shop)
                                        .menu(menu)
                                        .position(i + 1)
                                        .build();

                                signatureMenus.add(signatureMenu);
                                em.persist(signatureMenu);
                            }
                        });
            }

            return signatureMenus;
        }

        private List<Order> generateOrder(List<Shop> shops, List<Member> members, List<MenuGroup> menuGroups) {
            List<Order> orders = new ArrayList<>();

            for (Member member : members) {
                int n = 1 + random.nextInt(15);
                for (int i = 0; i < n; i++) {
                    Shop shop = shops.get(random.nextInt(shops.size()));

                    List<Menu> menus = new ArrayList<>();
                    menuGroups.stream()
                            .filter(menuGroup -> menuGroup.getShop() == shop)
                            .forEach(menuGroup -> menus.addAll(menuGroup.getMenus()));

                    List<OrderItem> orderItems = new ArrayList<>();
                    int m = 1+random.nextInt(3);
                    for (int j = 0; j < m; j++) {
                        Menu menu = menus.get(random.nextInt(menus.size()));

                        OrderItem orderItem = OrderItem.builder()
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
                    orders.add(order);
                    em.persist(order);
                }
            }

            return orders;
        }

        private List<Review> generateReview(List<Order> orders, List<Shop> shops, List<Member> members) {
            List<Review> reviews = new ArrayList<>();

            for (Order order : orders) {
                if (random.nextBoolean()) continue;

                Shop s = shops.stream()
                        .filter(shop -> order.getShop() == shop)
                        .findFirst().get();

                Review.ReviewBuilder reviewBuilder = Review.builder()
                        .order(order)
                        .content("맛있어요")
                        .quantityScore(5 * random.nextFloat())
                        .deliveryScore(5 * random.nextFloat())
                        .tasteScore(5 * random.nextFloat())
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
                    em.persist(reviewImage);
                }

                reviews.add(review);
                em.persist(review);
            }

            return reviews;
        }

        private String getFilePath(String filename) {
            return "/images/" + filename;
        }
    }
}
