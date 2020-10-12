# Diary_app 📖

2018년 2학기 모바일 응영 기말과제로 개발했던 안드로이드 일기장 어플 **하루 일기**입니다.✨

gradle 설정이 맞지 않거나 일부 api 서비스가 중단되어 있습니다

This is a diary application developed as a final assignment.
Some services may not work because it is an outdated version.



## 기능 설명

1. **잠금 화면**

   ![image](https://user-images.githubusercontent.com/62419307/93488209-0dec4580-f941-11ea-9113-49320a5ce5b6.png)

   - DB에 COL_PW라는 컬럼을 지정해 앱의 암호를 설정할 수 있습니다. 
   - 암호 설정과 재설정을 두어 암호를 등록하도록 하였고, DB의 값과 비교하여 잠금을 풀 수 있습니다.

   <br>

2.  **일기 쓰기**

   ![image](https://user-images.githubusercontent.com/62419307/93486311-0461de00-f93f-11ea-883e-0f943615bbde.png)

   ![image](https://user-images.githubusercontent.com/62419307/93486829-9833aa00-f93f-11ea-977c-bb2495804bce.png)

   - 날짜는 DatePicker를 통해 설정할 수 있습니다. 

   - **오늘의 날씨** 항목을 더블 클릭하면 Dialog로 직접 입력 / 날씨 불러오기를 선택할 수 있습니다. 
      <br>
     ✅ 날씨 불러오기의 경우, 기상청 API를 통해 현재 기온 값을 가져옵니다.

   - **오늘의 위치**를 더블 클릭하면 Dialog로 직접 입력 / 위치 불러오기를 선택할 수 있습니다. <br>
     ✅ 위치 불러오기의 경우, 지오코딩을 통해 현재 위치 값을 가져옵니다.

   - **오늘의 영화** 입력창을 더블 클릭하면 네이버 영화 API를 통해 영화를 검색할 수 있습니다. 

   - **오늘의 기분** 입력창을 더블 클릭하면 수치 값을 선택할 수 있는 Dialog가 뜨고 선택한 값만큼 배터리 잔량으로 기분을 표시할 수 있습니다.

   - 옵션메뉴의 글씨 색 / 타입 지정을 통해 **오늘의 일기** 내용의 색깔과 타입을 지정할 수 있습니다.

     <br>

3. **일기 모아보기**

   ![image](https://user-images.githubusercontent.com/62419307/93487471-463f5400-f940-11ea-91d5-4b209389216c.png)

   - 일기는 월별로 모아보거나 달력으로 모아서 볼 수 있습니다.
   - 일기 검색이 가능합니다.<br>
     ✅ Filter 인터페이스를 구현하여 입력창에 값이 입력될 때마다 해당 입력 값이 포함된 결과 값만 리스트 뷰에 필터링이 가능합니다.
