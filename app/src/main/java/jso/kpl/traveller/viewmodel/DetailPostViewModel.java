package jso.kpl.traveller.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import jso.kpl.traveller.App;
import jso.kpl.traveller.model.Post;
import jso.kpl.traveller.model.ResponseResult;
import jso.kpl.traveller.model.SmallPost;
import jso.kpl.traveller.network.CartlistAPI;
import jso.kpl.traveller.network.RouteOtherDetailAPI;
import jso.kpl.traveller.network.WebService;
import jso.kpl.traveller.ui.EditingMsg;
import jso.kpl.traveller.ui.Fragment.ImageSideItem;
import jso.kpl.traveller.ui.adapters.ImageSideVpAdapter;
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter;
import jso.kpl.traveller.ui.adapters.RouteOtherDetailCategoryItemAdapter;
import jso.kpl.traveller.util.CurrencyChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPostViewModel extends ViewModel implements Callback {

    String TAG = "Trav.DetailPostViewModel.";

    public String title = "포스트";

    //해당 포스트의 라이브 데이터
    public MutableLiveData<Post> postItem = new MutableLiveData<>();

    //스몰 포스트가 불렸을 때 들어갈 스몰 포스트 데이터의 라이브 데이터
    public MutableLiveData<SmallPost> smallPostItem = new MutableLiveData<>();

    //카테고리 리사이클러뷰의 어댑터
    public MutableLiveData<RouteOtherDetailCategoryItemAdapter> categoryAdapter = new MutableLiveData<>();

    //본인 포스트 체크 라이브 데이터
    public MutableLiveData<Boolean> isUser = new MutableLiveData<>();

    //좋아요 체크 라이브 데이터
    public MutableLiveData<Boolean> isLike = new MutableLiveData<>();
    //카트 리스트 체크 라이브 데이터
    public MutableLiveData<Boolean> isCart = new MutableLiveData<>();

    public CartlistAPI cartlistAPI;

    //--------------------
    // 포스트 하위 모든 이미지들을 보여주는 객체
    public ImageSideVpAdapter postImgAdapter;

    public ImageSideVpAdapter getPostImgAdapter() {
        return postImgAdapter;
    }

    public void setPostImgAdapter(ImageSideVpAdapter postImgAdapter) {
        this.postImgAdapter = postImgAdapter;
    }

    //통신------------------------------------------------------------------------------------------
    RouteOtherDetailAPI routeOtherDetailAPI = WebService.INSTANCE.getClient().create(RouteOtherDetailAPI.class);

    public DetailPostViewModel() {

        cartlistAPI = WebService.INSTANCE.getClient().create(CartlistAPI.class);
        isLike.setValue(false);
        isCart.setValue(false);
        isUser.setValue(false);
    }

    //루트 노드 어댑터
    public RouteNodeAdapter routeNodeAdapter;

    public RouteNodeAdapter getRouteNodeAdapter() {
        return routeNodeAdapter;
    }

    public void setRouteNodeAdapter(RouteNodeAdapter routeNodeAdapter) {
        this.routeNodeAdapter = routeNodeAdapter;
    }
    //----------------------------------------------------------------------------------------------

    public void onAddCartPost() {

        Call<ResponseResult<Integer>> call;

        //isCart true일 떄 삭제
        if(isCart.getValue()){
            call = cartlistAPI.removeCartList(App.Companion.getUser().getU_userid(), postItem.getValue().getP_id());
        }else {
            call = cartlistAPI.addCartList(App.Companion.getUser().getU_userid(), postItem.getValue().getP_id());
        }

        call.enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {

                if(response.body() != null){
                    ResponseResult<Integer> result = response.body();

                    if(result.getRes_type() == 1){
                        isCart.setValue(!isCart.getValue());
                    } else {
                        App.Companion.sendToast("카트 리스트에 추가 실패");
                    }

                }  else{

                    App.Companion.sendToast("카트 리스트에 추가 실패");
                }


            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {

                App.Companion.sendToast("카트 리스트에 추가 실패");

                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void onSendMsgClicked(){
        Intent intent = new Intent(App.INSTANCE, EditingMsg.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("p_author", postItem.getValue().getP_author());
        App.INSTANCE.startActivity(intent);
    }

    //좋아요 누를 때 클릭 이벤트
    public void onLikeClicked() {
        likeCall();
        Toast.makeText(App.INSTANCE, "좋아요 버튼 클릭", Toast.LENGTH_SHORT).show();
    }

    //포스트를 부르는 통신 함수
    public void postCall(int pId) {
        routeOtherDetailAPI.loadPost(App.Companion.getUser().getU_userid(), pId).enqueue(this);
    }

    //스몰 포스트를 부르는 통신 함수
    public void smallPostCall(int sp_id) {
        routeOtherDetailAPI.loadSmallPost(sp_id).enqueue(this);
    }

    //포스트를 부를 때
    public void loadingPost(Post post) {

        if (postItem == null)
            postItem = new MutableLiveData<>();

        postItem.setValue(post);

        updateRecentPostCall();

        isLike.setValue(postItem.getValue().isP_is_like());

        categoryAdapter.setValue(new RouteOtherDetailCategoryItemAdapter(postItem.getValue().getP_category(), 1));

        if(App.Companion.getUser().getU_nick_name().equals(postItem.getValue().getP_author()))
            isUser.setValue(true);

        Log.d(TAG, "본인 포스트: " + isUser.getValue());

        // 기간 텍스트 설정
        isLike.setValue(postItem.getValue().isP_is_like());

        isCart.setValue(postItem.getValue().isP_is_cart());

        if (!postItem.getValue().getP_expenses().contains("₩"))
            postItem.getValue().setP_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(postItem.getValue().getP_expenses())));

        post.getP_sp_list();

        for (int i = 0; i < postItem.getValue().getP_sp_list().size(); i++) {

            for (int j = 0; j < postItem.getValue().getP_sp_list().get(i).getSp_imgs().size(); j++) {

                ImageSideItem item = new ImageSideItem();

                Bundle bundle = new Bundle();
                bundle.putString("img", postItem.getValue().getP_sp_list().get(i).getSp_imgs().get(j));
                item.setArguments(bundle);

                getPostImgAdapter().addItem(item);
            }
        }

        if (getPostImgAdapter().getCount() == 0) {
            ImageSideItem item = new ImageSideItem();

            Bundle bundle = new Bundle();
            bundle.putString("img", "");
            item.setArguments(bundle);

            getPostImgAdapter().addItem(item);
        }

        getPostImgAdapter().notifyDataSetChanged();

        for (int i = 0; i < post.getP_sp_list().size(); i++) {
            SmallPost smallPost = post.getP_sp_list().get(i);

            if (!smallPost.getSp_expenses().contains("₩"))
                smallPost.setSp_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(smallPost.getSp_expenses())));

            getRouteNodeAdapter().putItem(smallPost);
        }
        Log.d(TAG + "Post 내용 : ", postItem.getValue().toString());
    }

    public void loadingSmallPost(SmallPost smallPost) {

        smallPostItem.setValue(smallPost);

        Log.d(TAG, "loadingSmallPost: " + smallPostItem.getValue().toString());

        if (!smallPostItem.getValue().getSp_expenses().contains("₩"))
            smallPostItem.getValue().setSp_expenses(CurrencyChange.moneyFormatToWon(Long.parseLong(smallPostItem.getValue().getSp_expenses())));

        categoryAdapter.setValue(new RouteOtherDetailCategoryItemAdapter(smallPostItem.getValue().getSp_category(), 1));

        Log.d(TAG, "이미지 리스트 사이즈: " + smallPostItem.getValue().getSp_imgs().size());


        // img list에 표시할 데이터 리스트 생성
        if (smallPostItem.getValue().getSp_imgs().size() > 0) {

            for (int i = 0; i < smallPostItem.getValue().getSp_imgs().size(); i++) {
                ImageSideItem item = new ImageSideItem();
                Bundle bundle = new Bundle();
                bundle.putString("img", smallPostItem.getValue().getSp_imgs().get(i));

                item.setArguments(bundle);
                getPostImgAdapter().addItem(item);
            }
        } else {
            if (getPostImgAdapter().getCount() == 0) {
                ImageSideItem item = new ImageSideItem();

                Bundle bundle = new Bundle();
                bundle.putString("img", "");
                item.setArguments(bundle);

                getPostImgAdapter().addItem(item);
            }
        }

        getPostImgAdapter().notifyDataSetChanged();

        Log.d(TAG + "SmallPost 내용 : ", smallPostItem.getValue().toString());

    }

    public void likeCall() {

        routeOtherDetailAPI.likePost(App.Companion.getUser().getU_userid(), postItem.getValue().getP_id(), isLike.getValue()).enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                if (response.body() != null) {

                    ResponseResult<Integer> result = response.body();

                    if (result.getRes_type() == 1) {
                        if (isLike.getValue()) {
                            postItem.getValue().setP_is_like(!isLike.getValue());
                            isLike.setValue(!isLike.getValue());
                            App.Companion.sendToast("좋아요를 해제했습니다.");
                        } else {
                            postItem.getValue().setP_is_like(!isLike.getValue());
                            isLike.setValue(!isLike.getValue());
                            App.Companion.sendToast("좋아요를 했습니다.");
                        }
                    } else {
                        App.Companion.sendToast("일시적 에러로 인해 실패했습니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                App.Companion.sendToast("일시적 에러로 인해 실패했습니다.");
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    // 마이 페이지의 최근 포스트를 등록하는 통신 함수
    public void updateRecentPostCall() {

        routeOtherDetailAPI.updateRecentPost(App.Companion.getUser().getU_userid(), postItem.getValue().getP_id()).enqueue(new Callback<ResponseResult<Integer>>() {
            @Override
            public void onResponse(Call<ResponseResult<Integer>> call, Response<ResponseResult<Integer>> response) {
                if (response.body() != null) {
                    if (response.body().getRes_obj() == 0) {
                        updateRecentPostCall();
                        Log.d(TAG, "실패 -> 최근 포스트 재등록");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Integer>> call, Throwable t) {
                Log.e(TAG, "Log.d(TAG, \"실패 -> 최근 포스트 재등록\");", t);
                updateRecentPostCall();
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(TAG + "통신 성공", "성공적으로 전송");

        if (response.body() != null) {

            boolean type = ((ResponseResult<List<?>>) response.body()).getRes_obj().get(0) instanceof Post;

            if (type) {
                //포스트 상세 내용
                if (((ResponseResult<List<?>>) response.body()).getRes_type() == 1) {
                    App.Companion.sendToast("포스트 상세 보기");
                    loadingPost(((ResponseResult<List<Post>>) response.body()).getRes_obj().get(0));
                } else {
                    App.Companion.sendToast("포스트 상세 보기 실패");
                }
            } else {
                if (((ResponseResult<List<?>>) response.body()).getRes_type() == 1) {
                    App.Companion.sendToast("스몰 포스트 상세 보기");
                    Log.d(TAG, "스몰 포스트 상세보기");
                    loadingSmallPost(((ResponseResult<List<SmallPost>>) response.body()).getRes_obj().get(0));
                } else {
                    App.Companion.sendToast("스몰 포스트 상세 보기 실패");
                    Log.d(TAG, "스몰 포스트 상세보기 실패");
                }
            }
        } else {
            Log.d(TAG, "스몰 포스트 상세보기 실패");
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Toast.makeText(App.INSTANCE, "통신 불량" + t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.d(TAG + "통신 실패", "틀린 이유: " + t.getMessage());
        t.printStackTrace();
    }

    @Override
    public void onCleared() {
        super.onCleared();
    }
}
