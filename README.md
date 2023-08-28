# 재고 시스템 동시성 이슈 스터디
- 둘 이상의 스레드가 동일 데이터에 접근하여 동시에 변경하려고 하는 경우 데이터 정합성 문제 발생 할 수 있음
- 이런 케이스를 해결 하기 위해 아래 방안이 있으며, 해당 이슈 해결 스터디를 위한 테스트 코드

## 해결 방안
1. Java
   - synchronized
2. MySQL
   - Pessimistic lock
   - Optimistic lock
   - Named lock
3. Redis
   - Lettuce
   - Redisson
