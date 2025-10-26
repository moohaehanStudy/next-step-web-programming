
$(document).ready(function(){/* jQuery toggle layout */
  $('#btnToggle').click(function(){
    if ($(this).hasClass('on')) {
      $('#main .col-md-6').addClass('col-md-4').removeClass('col-md-6');
      $(this).removeClass('on');
    }
    else {
      $('#main .col-md-4').addClass('col-md-6').removeClass('col-md-4');
      $(this).addClass('on');
    }
  });
  $(".answerWrite input[type=submit]").click(addAnswer);

  $(".qna-comment").on("click", ".form-delete", deleteAnswer);
});

function onSuccess(json, status){
  // HTML 문서 안에 id="answerTemplate"인 요소를 찾는다
  // $("#answerTemplate").html()은 요소 안의 HTML 내용을 문자열로 가져온다
  // 즉, 템플릿으로 사용할 HTML 문자열을 불러온다
  var answerTemplate = $("#answerTemplate").html();

  //위에서 불러온 HTML 문자열(answerTemplate)안에 {0}, {1}, {2}, {3} 같은 포맷용 자리 표시자가 있다면 .format() 함수를 이용해 실제 값으로 치환한다
  var template = answerTemplate.format(json.writer, new Date(json.createdDate), json.contents, json.answerId);

  // class="qna-comment-slipp-articles"인 요소를 찾아서, 가장 앞쪽에(prepend) 새로 만든 HTML(template)을 삽입한다
  //즉, 새로운 답변이 화면 상단에 추가되는 효과를 준다
  $(".qna-comment-slipp-articles").prepend(template);
}

function deleteAnswer(e) {
  e.preventDefault();

  var deleteForm = $(e.target).closest("form");
  var queryString = deleteForm.serialize();

  $.ajax({
    type: 'post',
    url: "/api/qna/deleteAnswer",
    data: queryString,
    dataType: 'json',
    error: function(xhr, status){
      alert("error");
    },
    success: function(json, status){
      if (json.status == true) {
        deleteForm.closest('article').remove();
      }
    }
  });
}

function addAnswer(e) {
  e.preventDefault(); //submit이 자동으로 동작하는 것을 막는다
  //form 데이터들을 자동으로 묶어준다
  var queryString = $("form[name=answer]").serialize();

  $.ajax({
    type: 'post',
    url: "/api/qna/addAnswer",
    data: queryString,
    dataType: 'json',
    error: onError,
    success: onSuccess,
  });
}

function onError(xhr, status, error) {
  console.error("Ajax 요청 실패:", status, error);
  console.error("서버 응답 내용:", xhr.responseText);

  // 사용자에게 알림창으로 표시할 수도 있습니다.
  alert("답변 등록 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
}

//이걸 정의하면, 모든 문자열 객체에서 .format() 메서드를 쓸 수 있게 된다qh
//"안녕하세요 {0}님".format("소윤"); // → "안녕하세요 소윤님"
String.prototype.format = function(){
  //arguments는 함수로 전달된 모든 인수를 배열처럼 담고 있다
  var args = arguments;

  //this.replace
  // 정규식 {(\d+)}는 {0}, {1}, {2} 같은 숫자 포맷 패턴을 찾는다. g 플래그는 문자열 전체에서 모든 일치 항목을 바꾸겠다는 의미이다
  // function(match, number) 콜백은 각 {0}, {1} 이 일치할 때마다 호출된다
  // match: 실제 {0} 같은 전체 문자열, number: 괄호 속의 숫자
  // args[number]를 꺼내서 {0} => 첫번째 인자, {1} => 두번째 인자 식으로 바꾼다
  return this.replace(/{(\d+)}/g, function(match, number){
    return typeof args[number] != 'undefined' ? args[number] : match;
  });
};