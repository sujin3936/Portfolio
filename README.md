# HappyMine
헬스장 회원을 위한 모바일 솔루션으로, 직관적인 사용자 인터페이스를 통해 최적의 편의성을 제공하는 안드로이드 앱

1.프로젝트 개요

1.1 제목  
    HappyMine: 헬스장 이용 프로그램

1.2 개발도구 
    Android Studio Giraffe, firebase

1.3 프로그램 주요 기능

    1) 앱 설명
	해피마인 앱은 헬스장 회원을 위한 모바일 솔루션으로, 직관적인 사용자 인터페이스를 통해 사용자에게 최적의 편의성을 제공함.
    2) 앱의 특징
	해피마인 앱은 기존 헬스장 회원 관리 시스템의 복잡성을 개선하여 간결한 인터페이스를 제공함. 이 앱은 매번 번거로운 회원권 만료일 확인과 헬스장의 과도한 세일즈로 인한 불편함을 해소하고자 만료일을 사용자가 직접 쉽	게 확인할 수 있게 하였음. 또한, 언제 어디서나 비대면으로 회원권을 간편	하게 구매할 수 있는 기능을 제공하여 사용자의 편의를 최우선으로 고려함.
    3) 주요 기능
	입장체크, 상품구매 기능 제공

 1.4 UX 설계
![image](https://github.com/user-attachments/assets/42e43ef4-f706-4269-af22-86ffe203fb9d)
![image](https://github.com/user-attachments/assets/4ed4999b-ea5b-474d-b883-d7ad42d6292f)
![image](https://github.com/user-attachments/assets/23e6cdac-b61a-4f2e-99bb-7896e492df58)
![image](https://github.com/user-attachments/assets/051cadf4-22d7-43ad-9e4d-1a9205793196)

1.5 DB 설계
Firebase 사용으로 관계형 데이터베이스가 아닌 실시간 데이터베이스 사용
![image](https://github.com/user-attachments/assets/ef10f706-c93f-4516-a3fc-50544cfa5f37)

2.구현한 클래스 목록

2.1 프로젝트 클래스 구조

![image](https://github.com/user-attachments/assets/b9b397bf-6a4b-40fa-8dc4-345b0be1557e)


2.2 클래스별 기능

    1) Activity
    SplashActivity: 앱 시작 시 로고를 띄워주는 기능
    Login: 로그인 처리 기능
    Signup: 회원가입 처리 기능
    MainActivity: 네비게이션 드로어를 통해 여러 프래그먼트로 이동하고 로그아웃      기능을 제공

    2) Application
    CustomApplication: 전역 변수 사용 및 애플리케이션 초기화

    3) Fragment
    CheckFragment: 입장 체크 기능
    CheckSuccessFragment: 입장 체크 성공 시 이용권 정보 표시 기능
    GoodsBuyFragment: 이용권 구매 기능
    AgreeFragment: 이용약관 표시 및 동의 처리
    
    4) ViewModel
    GoodsBuyViewModel: 이용권 구매 화면의 데이터 저장 후 복원 기능

    5) Utility class
    CommonF: 날짜 선택 다이얼로그, 사용자 이름 가져오기의 공통 메소드 제공


3.참고자료

3.1 활용한 AI 및 오픈 소스 목록: 
ChatGPT 버전 4, Firebase 문서

3.2 활용 방법 및 출처

    1) 회원가입, 로그인 기능 코드 참고
       출처: https://loasd.tistory.com/60?category=1088303
    2) 데이터 추가 및 읽기 코드 참고
       출처: Firebase 문서
    3) ViewModel 데이터 저장 오류 해결 코드 참고
       출처: ChatGPT 
    4) NavController 사용 방법 참고
       출처: https://jae-young.tistory.com/46
    5) 입장 기록 배열로 저장 방법 참고
       출처: ChatGPT

4.프로젝트 개발 후기

4.1 프로젝트를 통해 배운 점

이번 프로젝트를 통해 모바일 앱 설계부터 구현 단계까지 모두 경험하며 여러 가지 난관을 만나게 되었습니다. 첫 번째 난관은 기존에 사용하던 empty views activity에서 Navigation Drawer를 구현하려고 할 때 발생했습니다. 계속 오류가 나고 오류의 원인을 찾지 못했지만, Navigation Drawer Views Activity로 변경한 후 보다 쉽게 해결할 수 있었습니다. Navigation Drawer 기능이 기본적으로 구현되어 있는 Activity가 있다는 새로운 사실을 알게 되었습니다.
두 번째 난관은 기존에 배웠던 MySQL 데이터베이스 대신 Firebase를 선택하며 아무 지식이 없는 상태에서 공부를 시작한 것입니다. 관계형 데이터베이스가 아닌 실시간 데이터베이스 처리 방법도 익숙하지 않았지만, 공식 문서를 참고하며 점점 익숙해졌고, Firebase가 간단한 데이터베이스라는 것을 알게 되었습니다. 처음 계획했던 앱 설계와 동일한 수준으로 구현하여 뿌듯하면서도 더 욕심이 났습니다. 기간이라는 제한 때문에 더 많은 기능을 추가하지는 못했지만, 추후 개인적으로도 기능들을 더 추가해 나갈 예정입니다.

4.2 프로그램 개선점

    1) 비밀번호 찾기 기능
      현재 비밀번호 찾기 기능이 구현되지 않아 사용자가 비밀번호를 잊어버렸을 때 로그인이 어렵습니다. 같은 전화번호로 이미 계정이 있다고 판단되어 회원가입 또한 불가능합니다. 이 문제를 해결하기 위해 비밀번호 찾기 기능을 추가해야 합니다.
    2) 회원가입 시 비밀번호 작성 규칙 강화
      회원가입 시 비밀번호 작성 규칙이 명시되어 있지만, 제대로 확인하지 않는 사용자가 많습니다. 이를 개선하기 위해 비밀번호 입력 시 실시간으로 규칙을 확인하고, 사용자에게 적절한 피드백을 제공하는 예외 처리를 추가적으로 구현해야 합니다.
