# myselectshop

Security에서 JWT를 사용한 인증/인가
http://localhost:8080/api/user/login-page 로그인, 회원가입 화면에 모두가 접근 가능하게 
- 이외의 url은 접근이 불가능 (401에러를 내보냄)
- 관리자가 아닌 회원은 로그인 후 다른 페이지로 넘어갈 수 없음 (403에러를 내보냄)


예외처리
http://localhost:8080/api/shop 폴더 중복으로 넣을 수 없게 예외처리 (F12에서 확인할 수 있음)

Global 예외 처리
http://localhost:8080/api/shop 상품 100원 이하로 등록 못 하게 (F12에서 확인할 수 있음)
