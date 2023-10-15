<template>
  <div class="dashboard-container">
    <h2 style="text-align: center;transform: translate(0px,-20px);">
      欢迎来到后台管理系统!
    </h2>
    <img src="https://image.itbaima.net/images/173/image-20231015123585386.jpeg"
         alt="图片加载失败" style="width: 1300px;height: 100%;transform: translate(-45px,-30px);"/>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
