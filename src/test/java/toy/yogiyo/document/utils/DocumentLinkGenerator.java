package toy.yogiyo.document.utils;

public class DocumentLinkGenerator {

    public static String generateLinkCode(DocUrl docUrl) {
        return String.format("link:common/%s.html[%s %s,role=\"popup\"]", docUrl.pageId, docUrl.text, "코드");
    }

    static String generateText(DocUrl docUrl) {
        return String.format("%s %s", docUrl.text, "코드명");
    }

    public enum DocUrl {
        DAYS("days", "영업 요일"),
        OPTION_TYPE("optionType", "옵션 유형")
        ;

        private final String pageId;
        private final String text;

        DocUrl(String pageId, String text) {
            this.pageId = pageId;
            this.text = text;
        }
    }
}
