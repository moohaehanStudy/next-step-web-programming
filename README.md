## Upstream 설정 & 동기화 가이드
test

프로젝트를 진행하면서 원본 레포의 최신 코드를 계속 반영하기 위해 아래 과정을 따릅니다. 

### 1. Upstream 등록 (최초 1회)
내 로컬 저장소에서 원본 레포를 upstream으로 등록한다.
```
git remote add upstream https://github.com/팀레포.git
git remote -v   # 등록 확인
```
- origin -> 내가 fork한 개인 레포
- upstream -> 팀에서 관리하는 원본 레포

### 2. 원본 레포 최신 변경 사항 가져오기
원본 레포의 변경 사항을 로컬로 가져옵니다.
```
git fetch upstream
```
- 변경 사항만 다운로드
- 내 로컬 브랜치는 그대로 유지
- 이후에 직접 merge하거나 pull로 병합해야함

### 3. 로컬 브랜치 업데이트하기
```
git pull upstream <브랜치명>
```
- fetch + merge 자동 실행
- 원본<브랜치명> 변경 사항이 내 로컬 브랜치에 병합됨

### 브랜치 명
최초 브랜치는 main 브랜치를 base로 `soyun-3`처럼 "각자 이름-챕터명"으로 하기!<br>
그 이후부터는 직전챕터 브랜치를 base로 하면 됩니다!
