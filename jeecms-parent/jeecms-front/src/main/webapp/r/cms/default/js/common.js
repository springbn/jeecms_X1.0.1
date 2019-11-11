$(window).ready(function(){
	$('.cate-all__button').hover(function() {
		$('.cate-box').show()
	},function () {
		$('.cate-box').hide()
	})
$('.cate-box').hover(function() {
	$(this).show()
},function () {
	$(this).hide()
})
  //顶部品牌样式隐藏
$('.cf-drop-btn').click(function(){
	$('#cfswitch').toggleClass('cf-tr-hidden');
	$('.cf-drop-btn i').toggleClass('fa-angle-down fa-angle-up');
	$('#cf-unwrap').toggle();
	$('.cf-switch__prev').toggle();
	$('.cf-switch__next').toggle();
});
 //关注中心 关注商品
 $('.att-more').click(function(){
	 $('.att__goodscon').toggleClass('att__goodscon--hei');
	 $('.att-goods-clsify').toggleClass('att-goods-clsify--hide');
 });

 
 /* 账户余额主页 */
 $('.bacem-links>span').click(function () { 
		$('.bacem-links>span').removeClass('bacem-link--active');
		$(this).addClass('bacem-link--active');
	});

// 店铺街全部分类  查看跟多
$('.allbrand__lookMore').click(function(){
	$('.allbrand__items').toggleClass('allbrand__items--height');
});

})
