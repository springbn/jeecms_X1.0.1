var gulp = require('gulp');
var autoprefixer = require('gulp-autoprefixer'); // 处理css中浏览器兼容的前缀  
var rename = require('gulp-rename'); //重命名  
var cssnano = require('gulp-cssnano'); // css的层级压缩合并
var sass = require('gulp-sass'); //sass
var jshint = require('gulp-jshint'); //js检查 ==> npm install --save-dev jshint gulp-jshint（.jshintrc：https://my.oschina.net/wjj328938669/blog/637433?p=1）  
var uglify = require('gulp-uglify'); //js压缩  
var concat = require('gulp-concat'); //合并文件  
var imagemin = require('gulp-imagemin'); //图片压缩 
var browserSync = require('browser-sync').create();
var reload = browserSync.reload;
var Config = require('./gulpfile.config.js');
var fileinclude  = require('gulp-file-include');
//======= gulp dev 开发环境下 ===============
function dev() {
    /** 
     * HTML处理 
     */
    gulp.task('html:dev', function () {
        return gulp.src(Config.html.src)
        .pipe(fileinclude({
            prefix: '@@',
            basepath: '@root'
          }))
        .pipe(gulp.dest(Config.html.dist)).pipe(reload({
            stream: true
        }));
    });
    /** 
     * assets文件夹下的所有文件处理 
     */
    gulp.task('assets:dev', function () {
        console.log('....')
        return gulp.src([Config.assets.src,'!./src/assets/css/*.scss']).pipe(gulp.dest(Config.assets.dist)).pipe(reload({
            stream: true
        }));
    });
    /** 
     * CSS样式处理 
     */
    gulp.task('css:dev', function () {
        return gulp.src(Config.css.src).pipe(gulp.dest(Config.css.dist))
    });
    /** 
     * SASS样式处理 
     */
    gulp.task('sass:dev', function () {
        return gulp.src(Config.sass.src).pipe(sass()).pipe(gulp.dest(Config.sass.dist))
    });
    /** 
     * js处理 
     */
    gulp.task('js:dev', function () {
        return gulp.src(Config.js.src).pipe(gulp.dest(Config.js.dist)).pipe(reload({
            stream: true
        }));
    });
    /** 
     * 图片处理 
     */
    gulp.task('images:dev', function () {
        // return gulp.src(Config.img.src).pipe(imagemin({
        //     optimizationLevel: 3
        //     , progressive: true
        //     , interlaced: true
        // })).pipe(gulp.dest(Config.img.dist)).pipe(reload({
        //     stream: true
        // }));
    });
    gulp.task('dev', ['css:dev', 'sass:dev',], function () {
        // Watch .css files  
        gulp.watch(Config.css.src, ['css:dev']);
        // Watch .scss files  
        gulp.watch(Config.sass.src, ['sass:dev']);
        console.log('编译完成')
     
    });
}
//======= gulp dev 开发环境下 ===============
module.exports = dev;