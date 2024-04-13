package toy.yogiyo.core.shop.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import toy.yogiyo.common.converter.StringArrayConverter;
import toy.yogiyo.common.domain.BaseTimeEntity;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.review.domain.Review;
import toy.yogiyo.core.category.domain.CategoryShop;
import toy.yogiyo.core.owner.domain.Owner;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_owner_id", columnList = "owner_id"))
@DynamicInsert
public class Shop extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    private String name;

    private long orderNum;
    private long likeNum;
    private long ownerReplyNum;

    // 리뷰
    private long reviewNum;

    @Column(precision = 3, scale = 2)
    private BigDecimal tasteScore;
    @Column(precision = 3, scale = 2)
    private BigDecimal quantityScore;
    @Column(precision = 3, scale = 2)
    private BigDecimal deliveryScore;
    @Column(precision = 3, scale = 2)
    private BigDecimal totalScore;

    private String icon;
    private String banner;

    // 공지
    private String noticeTitle;
    private String ownerNotice;
    @Builder.Default
    @Convert(converter = StringArrayConverter.class)
    private List<String> noticeImages = new ArrayList<>();

    private String callNumber;
    private String address;

    private Double longitude;
    private Double latitude;

    private LocalDateTime closeUntil;

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryShop> categoryShop = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Owner owner;

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusinessHours> businessHours = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CloseDay> closeDays = new ArrayList<>();

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void updateCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public void updateNotice(String title, String notice, List<String> images) {
        this.noticeTitle = title;
        this.ownerNotice = notice;
        this.noticeImages = images;
    }

    public void updateBusinessHours(List<BusinessHours> businessHours) {
        this.businessHours.clear();
        for (BusinessHours businessHour : businessHours) {
            this.businessHours.add(businessHour);
            businessHour.setShop(this);
        }
    }

    public void updateLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateCloseDays(List<CloseDay> closeDays) {
        this.closeDays.clear();
        for (CloseDay closeDay : closeDays) {
            this.closeDays.add(closeDay);
            closeDay.setShop(this);
        }
    }

    public void increaseOrderNum(){
        this.orderNum++;
    }

    public void addReview(Review review) {
        this.reviewNum++;
        this.tasteScore = (this.tasteScore.multiply(BigDecimal.valueOf(this.reviewNum-1)).add(review.getTasteScore())).divide(BigDecimal.valueOf(reviewNum), 2, RoundingMode.HALF_UP);
        this.quantityScore = (this.quantityScore.multiply(BigDecimal.valueOf(this.reviewNum-1)).add(review.getQuantityScore())).divide(BigDecimal.valueOf(reviewNum), 2, RoundingMode.HALF_UP);
        this.deliveryScore = (this.deliveryScore .multiply(BigDecimal.valueOf(this.reviewNum-1)).add(review.getDeliveryScore())).divide(BigDecimal.valueOf(reviewNum), 2, RoundingMode.HALF_UP);
        this.totalScore = (this.totalScore.multiply(BigDecimal.valueOf(this.reviewNum-1)).add(review.getTotalScore())).divide(BigDecimal.valueOf(reviewNum), 2, RoundingMode.HALF_UP);
    }

    public void decreaseLikeNum() {
        this.likeNum--;
    }

    public void increaseLikeNum() {
        this.likeNum++;
    }

    public LocalDateTime getOpenTime(LocalDateTime dateTime) {
        Days tomorrowDays = Days.valueOf(dateTime.getDayOfWeek().name());

        LocalTime tomorrowOpenTime = this.getBusinessHours().stream()
                .filter((hours) -> hours.getDayOfWeek() == Days.EVERYDAY || hours.getDayOfWeek() == tomorrowDays)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BUSINESSHOURS_NOT_FOUND))
                .getOpenTime();

        return dateTime
                .withHour(tomorrowOpenTime.getHour())
                .withMinute(tomorrowOpenTime.getMinute())
                .withSecond(tomorrowOpenTime.getSecond());
    }

    public void updateCloseUntil(LocalDateTime closeUntil) {
        this.closeUntil = closeUntil;
    }
}


