package com.fasttime.global.exception;

import lombok.Getter;

/**
 *  여러 라이브러리들은 RuntimeException을 상속한 여러 Exception 들을 예외로 던집니다.
 *  그렇기 때문에 저희 프로젝트에서 내부적으로 비즈니스로직 실패에 의해 던져지는 예외들과
 *  여러 라이브러리 에러로 던져지는 예외들을 구분하기가 까다로운 현상이 발견되었습니다.
 *  <p/>
 *  이를 해결하기 위해서 비즈니스 로직 전용 클래스인 ApplicationException을 만들어 이를 상속해서 확장해나간다면
 *  ControllerAdvice 와 같은 예외 처리 로직에 더욱 편하게 필터링이 가능할 것이라 생각해 만들게 되었습니다.
 */
@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 테스트 시 불필요한 message 작성을 피하기 위해서 만든 API 입니다.
     * 되도록 {@link #ApplicationException(ErrorCode errorCode, String message)} 을 사용하는 것을 추천합니다
     * @param errorCode HttpResponse 에 에러 관련 메시지를 전송하기 위해 공통적으로 설정한 ErrorCode 입니다.
     * @author Nine-JH
     */
    protected ApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @param errorCode HttpResponse 에 에러 관련 메시지를 전송하기 위해 공통적으로 설정한 ErrorCode 입니다.
     * @param message 실제 로그에 적재할 Exception Message 입니다. 최대한 자세하게 작성하시면 디버깅에 편합니다.
     * @author Nine-JH
     */
    protected ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() == null) {
            return errorCode.getMessage();
        }

        return super.getMessage();
    }
}
