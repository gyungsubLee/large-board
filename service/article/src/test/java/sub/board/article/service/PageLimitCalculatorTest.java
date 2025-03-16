package sub.board.article.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PageLimitCalculatorTest {

    @Test
    void calculatePageLimitTest() {
        calculatePageLimitTest(1L, 30L, 10L, 301L);
        calculatePageLimitTest(7L, 30L, 10L, 301L);
        calculatePageLimitTest(10L, 30L, 10L, 301L);

        // movablePageCount 10을 초과하기 때문에, 다음 범위로 넘어간다.
        // 다음 11 ~20 페이지 까지 한번에 조회
        calculatePageLimitTest(11L, 30L, 10L, 601L );

        // 동일하게 20을 초괴하기 떄문에 다음 범위로 넘어간다.
        //  -> 21 ~ 30
        calculatePageLimitTest(21L, 30L, 10L, 901L );
    }

    void calculatePageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
        assertThat(result).isEqualTo(expected);
    }

}