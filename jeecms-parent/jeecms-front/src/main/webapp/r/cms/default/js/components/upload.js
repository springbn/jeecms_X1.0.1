

//前端上传通用组件
const cmsUploadTpl = `
<div>
<div class="cms-upload" @click="selectFile">
    <slot>
      <el-button type="primary" size="mini">上传</el-button>
    </slot>
    <input type="file" name='fileName' multiple="multiple" ref='uploadFile' class="cms-upload-input" @change="onUpLoad($event)" v-if="clearShow">
    </div>
    <div class="cms-prview" v-if="imgObject&&imgObject.resourceId&&!custom">
      <div class="cms-prview__inner">
        <div class="cms-prview__title"><i class="fa fa-close" @click="deleteImg"></i></div>
        <img :src="baseUrl+imgObject.fileUrl" alt="">
      </div>
    </div>
</div>
 `
Vue.component('cms-upload', {
  template: cmsUploadTpl,
  props: {
    value: [String, Number],
    action: {
      type: String,
      default: cms.url + '/member/upload/o_upload'
    },
    custom:{
      type:Boolean,
      default:false
    },
    resourceData:{//回填图片
      type:Object,
      default:function () {
        return {}
      }
    },
    data:{//自定义参数
      type:Object,
      default:function () {
        return {}
      }
    }
  },
  data() { // 数据
    return {
      clearShow: true,
      imgObject: {},
      uploadInfo: '',
      baseUrl: cms.url
    }
  },
  watch: {
    resourceData:{
      handler(){
         if(this.resourceData.id&&this.resourceData.id!=''){
            var obj={
              fileUrl:this.resourceData.url,
              resourceId:this.resourceData.id
            }
          this.imgObject=obj
         }
      },
      deep:true,
      immediate: true
    }
  },
  methods: {
    selectFile() { // 选择上传的文件
      this.$refs['uploadFile'].click()
    },
    onUpLoad(el) { //返回选中的上传文件
      // 返回上传的文件
      const files = el.target.files
      this.clearShow = false
      setTimeout(() => { //清除上次选中的文件
        this.clearShow = true
      }, 5)
      //这里做验证拦截
      let formData = new FormData()
      formData.append("uploadFile", files[0]);
      this.startUpLoad(formData)
    },
    deleteImg() {
      this.imgObject = {};
      this.$emit('input', '')
      this.$emit('change',null,this.data)
    },
    startUpLoad(formData) { //上传至服务器
      let self = this;
      if (cms.token && cms.token !== '') {
        $.ajax({
          headers: {
            'JSPGOU-Auth-Token': cms.token
          },
          url: this.action,
          type: "POST",
          data: formData,
          contentType: false,
          processData: false,
          success: function (res) {
            if (res.code === 200) {
              console.log(res)
              self.imgObject = res.data
              
              self.$emit('input', res.data.resourceId)
              self.$emit('change',res.data,self.data)
            } else {
              cms.alert(res.message,5)
            }
          },
          error: function () {
            cms.alert("上传失败!",5)
          }
        });
      } else {
        cms.alert("未登录",5)
      }
    },
  },
  created() {
    // 生命钩子
  },
  computed: {
    // 计算属性
  }
})

//地区联动选择
const areaTpl = `
<div>
<el-select style="width:130px" size="small" v-model="selectData.province" value-key="areaCode" @change="changeProvince">
<el-option label="请选择"  :value="defaultValue"  ></el-option>
  <el-option :label="item.areaName"  :value="item"  v-for="item in provinceList" :key="item.areaCode"></el-option>
</el-select> 
<el-select  style="width:130px" size="small"  v-model='selectData.city'  value-key="areaCode" @change="changeCity">
<el-option label="请选择"  :value="defaultValue"  ></el-option>
<el-option :label="item.areaName"  :value="item"  v-for="item in cityList" :key="item.areaCode"></el-option>
</el-select> 
<el-select  style="width:130px" size="small"  v-model='selectData.area' value-key="areaCode" @change="changeArea">
<el-option label="请选择"  :value="defaultValue" ></el-option>
<el-option :label="item.areaName"  :value="item"  v-for="item in areaList" :key="item.areaCode"></el-option>
</el-select>
</div>
`
Vue.component('cms-area-select', {
  template: areaTpl,
  props:{
     data:{
       type:Object,
       default:function () {
         return {}
       }
     }
  },
  data() {
    return {
      selectData: { //选中的区域信息
        province: {
          areaCode: "",
          areaName: ""
        },
        city: {
          areaCode: "",
          areaName: "",
        },
        area: {
          areaCode: "",
          areaName: ""
        }
      },
      defaultValue: {
        areaCode: "",
        areaName: ""
      },
      provinceList: [],
      cityList: [],
      areaList: []
    }
  },
  methods: {
    getAreaData(areaCode) { //向后台请求数据
      return new Promise((resolve, reject) => {
        cms.ajax({
          url: cms.url + '/member/memberaddress/getArea',
          data: {
            areaCode: areaCode
          }
        }, function (res) {
          resolve(res)
        })
      })
    },
    getProvinceList() { //获取省份列表
      let self = this;
      this.getAreaData().then(res => {
        self.provinceList = res.data
      })
    },
    getCityList(areaCode) { //获取城市列表
      let self = this;
      this.getAreaData(areaCode).then(res => {
        self.cityList = res.data
      })
    },
    getAreaList(areaCode) { //获取县区列表
      let self = this;
      this.getAreaData(areaCode).then(res => {
        self.areaList = res.data
      })
    },
    changeProvince(obj) { //改变省份
    
      if (obj.areaCode == '') { //省份为空时
        this.selectData.province = this.defaultValue
        this.selectData.city = this.defaultValue
        this.selectData.area = this.defaultValue
        this.cityList = []
        this.areaList = []
      } else {
        this.cityList = [] //把城市列表置空
        this.areaList = [] //把县区列表置空
        this.selectData.city = this.defaultValue //城市选中置空
        this.selectData.area = this.defaultValue //县市选中置空
        this.getCityList(obj.areaCode)
      }
      this.$emit('change', this.selectData) //通知父级页面
    },
    changeCity(obj) { //改变城市
      if (obj.areaCode == '') { //城市为空时
        this.selectData.city = this.defaultValue
        this.selectData.area = this.defaultValue
        this.areaList = []
      } else {
        this.areaList = [] //把县区列表置空
        this.selectData.area = this.defaultValue //县市选中置空
        this.getAreaList(obj.areaCode)
      }
      this.$emit('change', this.selectData) //通知父级页面
    },
    changeArea(obj) { //改变县区
      this.$emit('change', this.selectData) //通知父级页面
    }

  },
  watch: {
    data:{
      handler(){
        this.selectData=JSON.parse(JSON.stringify(this.data))
        this.getCityList(this.data.province.areaCode)
        this.getAreaList(this.data.city.areaCode)
     
      },
      deep:true
    }
  },
  created() {
    this.getProvinceList() //初次加载只获取省份
  }
})
