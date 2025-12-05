package io.github.hanhy06.embellishchat.parser;

public record SyntaxStructure(
//      TODO:토큰,스택 구조의 커스텀 파서 만들어야 함
//      TODO:동작 은 eval 같은거 쓰면 위험하니 현제와 같은 구조 쓰거나 적당히 바꿔서 씀
//      TODO:구조는 대략적으로 일케 중간고사 끝나고 알아서 구현하샘
        ContentVerifyType tokenType,
        String tokenStart,
        String tokenEnd,
        ContentVerifyType optionType,
        String optionStart,
        String optionEnd
) {
}
