package jso.kpl.traveller.ui.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.size
import jso.kpl.traveller.App
import jso.kpl.traveller.R
import jso.kpl.traveller.model.Route
import jso.kpl.traveller.model.SmallPost
import jso.kpl.traveller.model.ViewTag
import jso.kpl.traveller.ui.TestPage
import kotlinx.android.synthetic.main.custom_node.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RouteNodeAdapter
{

    //노드 클릭 시 해당 노드의 데이터를 전송
    lateinit var onNodeClickListener: OnNodeClickListener

    interface OnNodeClickListener {
        fun onNodeClicked(sp: SmallPost, pos: Int)
        fun onNodeLongClicked()
    }

    private var itemList : ArrayList<SmallPost> // 전체적으로 item을 추가하면 증가되는 리스트
    private var ctx: Context // 해당 화면의 context
    private var RootLinear:LinearLayout // customNode가 추가될 공간의 RootLinear
    private val MaxColumnCnt:Int = 3 // 한 줄의 최대 3개의 아이템이 들어감.
    private var CurrentColumn:Int = 0 // 현재 몇번째 줄인지
    private var LinearParams : LinearLayout.LayoutParams // CustomNode에 들어갈 LinearParams
    private var tag_view : HashMap<Any, View>
    private var idx_viewTag : HashMap<Int, ViewTag> // index - [viewTag = tag, parentTag]

    constructor(_ctx : Context, rootLinear : LinearLayout)
    {
        this.itemList = ArrayList<SmallPost>()
        this.ctx = _ctx
        this.RootLinear = rootLinear
        this.tag_view = HashMap<Any, View>()
        this.idx_viewTag = HashMap<Int, ViewTag>()

        //CustomNode의 높이 값
        this.LinearParams = LinearLayout.LayoutParams(0, dpToPx(80f))
        this.LinearParams.weight = 1f
        // QuestionMarkNode를 그려준다.
    //   drawQuestionMarkNode()
    }

    constructor(itemList: ArrayList<SmallPost>, _ctx: Context ,rootLinear : LinearLayout)
    {
        this.itemList = itemList
        this.ctx = _ctx
        this.RootLinear = rootLinear

        this.tag_view = HashMap<Any, View>()
        this.idx_viewTag = HashMap<Int, ViewTag>()

        this.LinearParams = LinearLayout.LayoutParams(0, dpToPx(80f))
        this.LinearParams.weight = 1f
        // QuestionMarkNode를 그려준다.
        //초기 값
   //     drawQuestionMarkNode()
    }

    fun getItemSize() : Int
    {
        return this.itemList.size
    }

    /*
       노드의 맨 마지막에 생성되는 QuestionMarkNode를 지워준다.
     */
    private  fun removeQestionMarkNode()
    {
        var lastidx:Int = itemList.size-1

//        Log.d(TAG, "Route location = " + _route.location + " // cost = " + _route.cost)
//        Log.d(TAG, "lastidx = " + lastidx)
//        Log.d(TAG, "before itemList size = " + itemList.size)

        itemList.removeAt(lastidx)
//        Log.d(TAG, "aftre itemList size = " + itemList.size)

        var targetLinear:LinearLayout =  (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(lastidx)?.parent_view_Tag))
        var targetView : View = targetLinear.findViewWithTag<View>(idx_viewTag.get(lastidx)?.node_tag)

//        Log.d(TAG, "targetLinear`s Tag = " + targetLinear.tag)
//        Log.d(TAG, "targetView`s Tag = " + targetView.tag)
//        Log.d(TAG, "before targetLinear`s size = " + targetLinear.size)
        targetLinear.removeView(targetView)
//        Log.d(TAG, "after targetLinear`s size = " + targetLinear.size)
//        Log.d(TAG, "before idx_viewTag`s size = " + idx_viewTag.size)
        idx_viewTag.remove(lastidx)
//        Log.d(TAG, "after idx_viewTag`s size = " + idx_viewTag.size)

        if(lastidx.rem(MaxColumnCnt) == 0)
        {
//            Log.d(TAG, "lastidx가 3의 배수입니다.")
//            Log.d(TAG, "before RootLinear의 자식 뷰 갯수 : " + RootLinear.size)
            RootLinear.removeView(targetLinear)
//            Log.d(TAG, "after RootLinear의 자식 뷰 갯수 : " + RootLinear.size)
            CurrentColumn -= 1
        }
        else
        {
            Log.d(TAG, "lastidx가 3의 배수가 아닙니다.")
        }
    }

    /**
     *  실질적으로 아이템을 추가하고 화면에 그려주는 메서드.
     *  화면에 그려주기 전에 아래와 같은 과정을 거쳐야한다.
     *
     *  코드 구조가 NodeAdpater를 생성하면 drawQuestionMarkNode() 메서드를 통해서 ?노드가 생성됨
     *  즉 모든 정상적인 Route 노드가 생성되기 위해서는 Route 노드가 생성되는 자리를 ?노드가
     *  자리 차지하고 있으므로 ?노드를 날려주어야한다.
     *
     *  1. ?노드를 날려주기 위하여 마지막 노드(lastidx)를 구하고 해당 인덱스로 itemList에 적재된 ?노드를 데이터를 지운다.
     *  2. ?노드가 어디에 삽입되었는지 알아내기 위해서 ?노드가 생성된 LinearLayout(targetLinear)을 parent_view_Tag를 통해서 찾고
     *  3. 해당 LinearLayout에 삽입된 ?노드(targetView)를 찾아낸다.
     *  4. targetLinear에서 targetView를 지워준다.
     *  5. idx_viewTag에서 ?노드의 index와 viewTag를 지워준다.
     *  6. lastidx가 3으로 나눠떨어지는 경우 새롭게 LinearLayout을 동적으로 생성된 경우기 때문에 RootLinear에서 지워준다.
     *  7. Linear가 생성되어 개행된 경우 CurrentColumn가 증가했으므로 -1을 해준다.
     */
    fun putItem(_route:SmallPost)
    {
//        if(itemList.size > 0)
//            removeQestionMarkNode()

        itemList.add(_route)
        var idx:Int = itemList.indexOf(_route)
        drawView(idx)
  //      drawQuestionMarkNode()

        Log.d(TAG, "줄: " + CurrentColumn)
    }

    fun updateNode(sp: SmallPost, pos: Int) {
        itemList[pos] = sp

        var node: View =
            (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(pos)?.parent_view_Tag)).findViewWithTag<View>(
                idx_viewTag.get(pos)?.node_tag
            )
        Log.d("Test.update", "업데이트 번호: " + pos + ", 내용" + itemList[pos])

        idx_viewTag[pos]
        if (sp.sp_place.isEmpty() || sp.sp_place.isBlank()) {
            //  node.locationText.setText("Blank")
        } else {
            node.locationText.setText(sp.sp_place)
        }

        if (sp.sp_place.isEmpty() || sp.sp_place.isBlank()) {
            //  node.costtv.setText("Blank")
        } else {
            node.costtv.setText(sp.sp_expenses)
        }
    }

    fun returnList():List<SmallPost>{

   //    itemList.remove(itemList.last())

        return itemList
    }

//    private fun drawQuestionMarkNode()
//    {
//        var qnode : SmallPostDialog = SmallPostDialog("?","?")
//        itemList.add(qnode)
//        var idx : Int = itemList.indexOf(qnode)
//        Log.d(TAG, "drawQuestionMarkNode, idx = " + idx)
//        drawView(itemList.indexOf(qnode))
//    }

    private fun drawView(index:Int)
    {
        // 행이 홀수든 짝수든 3으로 나눠떨어지는(나머지가 0인) index면 새로운 Linear를 RootLinear에 추가해줘야한다.
        if(index.rem(MaxColumnCnt) == 0)
        {
            var childLinear : (LinearLayout) = LayoutInflater.from(ctx).inflate(R.layout.skeleton,null, false) as LinearLayout
            childLinear.tag = View.generateViewId()
            tag_view.put(childLinear.tag, childLinear)

            Log.d(TAG, "childLinear tag = " + childLinear.tag)
            RootLinear.addView(childLinear)
            CurrentColumn += 1
        }

        //홀수행인 경우
        if((CurrentColumn%2) == 1)
        {
            // node의 생성 및 초기 설정
            var node : View = LayoutInflater.from(ctx).inflate(R.layout.custom_node, null, false) as View
            node.tag = View.generateViewId()
            node.layoutParams = this.LinearParams
            node.tailLine.visibility = View.INVISIBLE

            //node의 지역, 비용을 설정해준다.
            node.locationText.text = itemList.get(index).sp_place
            node.costtv.text = itemList.get(index).sp_expenses.toString()

            /*
                Tag를 통해서 내가 원하는 뷰를 추적할 수 있기 때문에 자신의 tag와 자기가 속하는 Linear의 tag를
                ViewTag라는 클래스에 담고 itemList의 index를 key로 HashMap에 저장한다.
            */
            idx_viewTag.put(index, ViewTag(node.tag, (RootLinear.get(RootLinear.size-1) as LinearLayout).tag))

            // 한줄에 들어가는 노드(3개) 중 제일 첫번째로 들어가는 노드(좌측 노드)
            if(index.rem(MaxColumnCnt) == 0)
            {
                //노드의 headline`s lineType 설정
                node.headLine.lineType = 2

                //첫번째 노드인 경우 => 첫번째 행인 경우 첫번째 노드의 headLine을 Invisible 해야한다.
                if(index == 0)
                {
                    node.headLine.visibility = View.INVISIBLE
                }
                else
                {
                    // 2번째 행부터는 LinearLayout의 가운데부터가 아닌 좌측, 우측부터 추가되어야 하기 때문에 weightSum을 설정해준다.
                    (RootLinear.get(RootLinear.size-1) as LinearLayout).weightSum = 3f
                    //이전 노드에서 invisible한 tailLine을 visible 해줘야한다.
                    visible_HeadLine_Previous_Node(index)
                }
            }
            //한줄에 들어가는 노드(3개) 중 두번째로 들어가는 노드(중간 노드)
            else if(index.rem(MaxColumnCnt) == 1)
            {
                //이전 노드에서 invisible한 tailLine을 visible 해줘야한다.
                visible_TailLine_Previous_Node(index)
            }
            //한줄에 들어가는 노드(3개) 중 세번째로 들어가는 노드(우측 노드)
            else if(index.rem(MaxColumnCnt) == 2)
            {
                node.tailLine.lineType = 0
                visible_TailLine_Previous_Node(index)
            }
            //노드를 Linear에 추가
            addNode(node, index)
        }
        //짝수행인 경우.
        else
        {
            /*
                2번째 행부터는 LinearLayout의 가운데부터 생성되는게 아니라 좌측, 우측부터 추가되어야 하기 때문에 weightSum을 설정해준다.
                LinearLayout.LAYOUT_DIRECTION_RTL = > RTL => Right To Left => 우측부터 좌측으로 추가된다.
             */
            (RootLinear.get(RootLinear.size-1) as LinearLayout).weightSum = 3f
            (RootLinear.get(RootLinear.size-1) as LinearLayout).layoutDirection = LinearLayout.LAYOUT_DIRECTION_RTL

            // node의 생성 및 초기 설정
            var node : View = LayoutInflater.from(ctx).inflate(R.layout.custom_node, null, false) as View
            node.tag = View.generateViewId()
            node.layoutParams = this.LinearParams
            node.headLine.visibility = View.INVISIBLE

            //node의 지역, 비용을 설정해준다.
            node.locationText.text = itemList.get(index).sp_place
            node.costtv.text = itemList.get(index).sp_expenses.toString()

            /*
                Tag를 통해서 내가 원하는 뷰를 추적할 수 있기 때문에 자신의 tag와 자기가 속하는 Linear의 tag를
                ViewTag라는 클래스에 담고 itemList의 index를 key로 HashMap에 저장한다.
            */
            idx_viewTag.put(index, ViewTag(node.tag, (RootLinear.get(RootLinear.size-1) as LinearLayout).tag))

            // 한줄에 들어가는 노드(3개) 중 제일 첫번째로 들어가는 노드(우측 노드)
            if(index.rem(MaxColumnCnt) == 0)
            {
                //노드의 tailline`s lineType 설정
                node.tailLine.lineType = 1
                visible_TailLine_Previous_Node(index)
            }
            //한줄에 들어가는 노드(3개) 중 두번째로 들어가는 노드(중간 노드)
            else if(index.rem(MaxColumnCnt) == 1)
            {
                visible_HeadLine_Previous_Node(index)
            }
            //한줄에 들어가는 노드(3개) 중 세번째로 들어가는 노드(좌측 노드)
            else if(index.rem(MaxColumnCnt) == 2)
            {
                node.headLine.lineType = 3
                visible_HeadLine_Previous_Node(index)
            }
            //노드를 Linear에 추가
            addNode(node, index)
        }
    }

    // 어차피 추가되는것은 RootLinear의 마지막 Child에게 추가될 것이기 때문에
    // RootLinear의 마지막 view에 추가한다.
    private fun addNode(_node :View, _idx:Int)
    {
        _node.nodeBtn.setOnClickListener(View.OnClickListener {
            //    Toast.makeText(ctx, "node location : " + _node.locationText.text + " cost val : " + _node.costtv.text, Toast.LENGTH_SHORT).show()

            //노드 클릭시 해당 노드가 가지고 있는 SmallPost의 데이터와 노드 인덱스를 전송
            onNodeClickListener.onNodeClicked(itemList[_idx], _idx)


        })
        _node.nodeBtn.setOnLongClickListener(View.OnLongClickListener{
            Toast.makeText(ctx, "해당 노드를 삭제합니다.", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "삭제 노드`s idx = " + _idx)
            Log.d(TAG, "삭제 노드`s location = " + _node.locationText.text)
            Log.d(TAG, "삭제 노드`s cost = " + _node.costtv.text)

            onNodeClickListener.onNodeLongClicked()

            removeItem(_idx)

            true
        })
        // ========== 여기까지 테스트 코드 =============
        (RootLinear.get(RootLinear.size-1) as LinearLayout).addView(_node)
    }

    /**
     *  화면에서 커스텀뷰를 삭제하는 메서드
     *  현재는 LongClickListener에 넣어둠.
     *
        qnode == QuestionMarkNode
        1. 일단 qnode를 삭제
        2. idx_viewTag랑 itemList의 인덱스 동기화를 위해서
         itemList.add(Route("?","?"))에 qnode를 삽입한다.(removeQuestionMarkNode 메서드에서 ItemList까지 삭제하는데
         밑에서 다시한번 qnode를 지워줄때 동기화가 안되면 에러가 나기 때문에 추가해준다... 어차피 밑의 두번째 removeQue~에서 지워짐)

         3. itemList에서 삭제할 노드의 아이템을 삭제한다.
         4. itemList에서 조정된 노드들을 view에 다시 그려주고 리스너를 초기화한다.
         5. 다시 qnode를 그려준다.
     */
    private fun removeItem(index:Int)
    {
        val targetIdx : Int = idx_viewTag.size-1
        Log.d(TAG, "print map start")
        printMap(idx_viewTag)

   //     removeQestionMarkNode()

        //QestionMark 데이터를 추가해준다.
        itemList.add(SmallPost("?","?"))

        itemList.removeAt(index)
        printItemList()
        printMap(idx_viewTag)

        removeQestionMarkNode()

        Log.d(TAG, "Twice Remove?")
        printItemList()
        printMap(idx_viewTag)

        for(i in index..targetIdx)
        {
            var tmpIdx : Int = i + 1
            if(idx_viewTag.size < tmpIdx) break

            var node : View = (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(i)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(i)?.node_tag)

            Log.d(TAG, "[i] = " + i + " // item = " + itemList.get(i).sp_place)
            node.locationText.text = itemList.get(i).sp_place
            node.costtv.text = itemList.get(i).sp_expenses.toString()

            // 리스너 초기화
            node.setOnClickListener(null)
            node.setOnLongClickListener(null)

            // 여기도 테스트 코드
            // 삭제는 removeItem(index)로 하면됨.
            // 일단 길게 누르면 삭제되게 만듬
            node.setOnClickListener({ Toast.makeText(ctx, "redraw node location : " + node.locationText.text + " cost val : " + node.costtv.text, Toast.LENGTH_SHORT).show()})
            node.setOnLongClickListener(
                {
                    removeItem(i)

                    onNodeClickListener.onNodeLongClicked()

                    true
                })
            //====== 여기까지 테스트 코드 ========

            var lastIndex:Int = itemList.size - 1;
            // 한줄에 들어가는 노드(3개) 중 제일 첫번째로 들어가는 노드(우측 노드)
          if(CurrentColumn % 2 == 1){

              Log.d(TAG, "홀수 선택값: " + index + ", " + "마지막: " + lastIndex )
              if(index == lastIndex + 1){
                  (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(index)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(index)?.node_tag).tailLine.visibility = View.INVISIBLE
              } else{
                  (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(lastIndex)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(lastIndex)?.node_tag).tailLine.visibility = View.INVISIBLE
              }

          } else{
              Log.d(TAG, "짝수 선택값: " + index + ", " + "마지막: " + lastIndex )
              if(index == lastIndex + 1){
                  (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(index)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(index)?.node_tag).headLine.visibility = View.INVISIBLE
              } else{
                  (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(lastIndex)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(lastIndex)?.node_tag).headLine.visibility = View.INVISIBLE
              }

          }


        }

  //      drawQuestionMarkNode()





        Log.d(TAG, "갯수: "+ getItemSize());

        printItemList()
    }
    /*
       홀수행의 노드들은 새로 추가할때 이전 노드의 tailLine의 visibility를 Invisible에서 Visible로 바꿔줘야한다.

       (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(index-1)?.parent_view_Tag))
           => 내가 찾으려는 노드(추가하려는 노드의 이전 노드)가 속한 LinearLayout를 parent_view_tag를 통해서 찾는다.
       .findViewWithTag<View>(idx_viewTag.get(index-1)?.node_tag).tailLine.visibility
           => 찾은 LinearLayout에서 내가 찾으려는 노드(추가하련느 노드의 이전 노드)의 tailLine의 visibility를 바꿔준다.
     */
    private fun visible_TailLine_Previous_Node(index: Int)
    {
        (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(index-1)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(index-1)?.node_tag).tailLine.visibility = View.VISIBLE
    }

    private fun visible_HeadLine_Previous_Node(index: Int)
    {
        (RootLinear.findViewWithTag<LinearLayout>(idx_viewTag.get(index-1)?.parent_view_Tag)).findViewWithTag<View>(idx_viewTag.get(index-1)?.node_tag).headLine.visibility = View.VISIBLE
    }

    companion  object {
        val TAG = "Demo.NodeAdapter"
    }

    fun dpToPx(dp: Float): Int {
        return dpToPx(dp, App.INSTANCE.resources)
    }

    //dp를 px로 변환
    private fun dpToPx(dp: Float, resources: Resources): Int {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        return px.toInt()
    }

    private fun printItemList()
    {

        for( route in itemList)
        {
            Log.d(TAG, "[${itemList.indexOf(route)}] , location : " + route.sp_place + "    cost : "+ route.sp_expenses)
        }
    }

    private fun printMap(mapval : HashMap<Int, ViewTag>)
    {
        val keys = mapval.keys.toIntArray()
        keys.sort()
        Log.d(TAG, "========= print Map =========")
        for(i in keys)
        {
//            Log.d(TAG, "[$i] : " + mapval.get(i))
              Log.d(TAG, "[$i] : " + mapval.get(i) + "// location : " + (RootLinear.findViewWithTag<LinearLayout>(mapval.get(i)?.parent_view_Tag)).findViewWithTag<View>(mapval.get(i)?.node_tag).locationText.text)
        }
        Log.d(TAG, "========= end =========")
    }
}