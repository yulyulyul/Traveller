package jso.kpl.traveller.model

/**
 * RouteNodeAdapter에서 뷰를 추가하거나 삭제할때 해당 뷰를
 * 컨트롤하기 위하여 tag를 이용함으로 자신의 tag와 자기가 속한 부모의 뷰의
 * tag를 담는 데이터 모델
 *
 * node_tag : 자신의 tag
 * parent_view_tag : 자신이 속한 부모 뷰의 tag
 */
data class ViewTag(var node_tag:Any, var parent_view_Tag:Any)