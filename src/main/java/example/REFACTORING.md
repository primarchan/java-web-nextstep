## 2장 문자열 계산기 구현을 통한 테스트와 리팩토링
* 각 단계의 개발 과정은 구현 => 테스트를 통해 결과 확인 => 리팩토링으로 진행
* 가능하면 위 3가지 요구사항으로 제시한 3가지 원칙을 지키도록 노력하면서 구현
* StringCalculator class의 경우, 리팩토링을 극단적으로 진행된 케이스
## 3장 개발 환경 구축 및 웹 서버 실습 요구사항
> * 저자 인용  
> "책의 경우 각 장별로 완벽한 내용을 쓰는 것을 목표로 하지 않는다, 그 보다는 1장부터 
> 끝까지 현재 내가 가지고 있는 역량으로 쓸 수 있는 수준까지 빠르게 끝마치는 것을 목표로 한다."
> 이와 같이 진행함으로써 한 번의 반복주기를 완료했다는 성취감을 느낄 수 있으며, 편집자 또는 기획자는
> 책의 전체 흐름이 기획 의도와 맞는지 빠른 시점에 파악하고 피드백을 줄 수 있다. 다음 반복주기에는
> 앞에서 해결하지 못한 부분을 추가 학습을 통해 해결하거나, 보완하는 방식이다.
> 이와 같이 몇 번의 반복주기를 통해 책 한 권을 완성하는 접근 방법이다. 이 책 또한 몇 번의
> 반복주기를 통해 완성했다.
> 학습 또한 같은 방법으로 접근할 수 있다. 각 주제에 대한 깊이는 깊지 않을 수 있지만
> 프로트엔드부터 백엔드까지 기능을 구현한 후 개발 서버(또는 실 서버)에 배포하는경험까지 
> 한 반복주기로 새아가하고 학습할 수 있다. 이와 같이 소프트웨어 전체 과정을 빠르게 경험함으로써
> 현재 상태에서 자신이 모르고 있는 부분이 무엇인지, 부족한 점이 무엇인지 빠르게 파악할 수 있다.
> 다음 반복주기는 자신이 가장 자신이 없거나 부족한 부분을 보완해 나가면서 새로운 기능을 추가해
> 나갈 수 있다. 이와 같이 접근할 경우 얻을 수 있는 이점 중의 하나는 자신이 무엇을 좋아하는지를
> 빨리 파악해 자신이 어느 분야의 전문가가 될 것인지에 대해서도 파악할 수 있다."  
> "두려움을 없애는 가장 좋은 방법은 반복함으로써 친숙해지는 방법 밖에 없다."