# nextjs 14

리액트 애플리케이션은 클라이언트 렌더링을 기본으로 한다.

- 클라이언트가 서버에 접속하면 ui 구성에 필요한 모든 자바스크립트 코드를 다운받고 이를 렌더한다.
- 초기 로딩에 시간이 오래 걸릴 수 있고, 번들 사이즈도 커진다.
- SEO(검색 엔진 최적화)에 불리하다. 클라이언트 자바스크립트가 ui를 완성하기 전까지 검색 엔진 봇이 볼 수 있는 정보가 한정적이다. html 뼈대 밖에 없다.

이를 해결하기 위해

- 코드 스플릿: 번들을 작은 단위(chunk)로 나눠서 클라이언트 전송한다..
- 웹팩이 많은 기능을 제공해주지만 설정에 관하여 많은 내용을 배워야하고, 수작업이 많이 들어간다.

### Nextjs

Nextjs는 서버사이드렌더링(SSR)과 정적사이트생성(SSG)을 지원하는 프레임워크로서, 코드 스플릿과의 통합이 잘 되어있다.
