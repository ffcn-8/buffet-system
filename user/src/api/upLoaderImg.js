//upLoaderImg.js
import axios from 'axios'		//引入axios
import { Toast } from 'vant'	//引入Toast
// 上传图片的接口
function upLoaderImg (file) {	//file为 你读取成功的回调文件信息
    //new 一个FormData格式的参数
    let params = new FormData()
    params.append('picture', file)
    let config = {
        headers: { //添加请求头
            'Content-Type': 'multipart/form-data'
        }
    }
    return new Promise((resolve, reject) => {
        //把 uploadUrl 换成自己的 上传路径
        axios.post('/api/upload', params, config).then(res => {
            // console.log('res17', res)
            if (res) {				//如果为真 resolve出去
                resolve(res.data)
            } else {
                //否则 Toast 提示
                Toast.fail('上传失败')
                reject(res.data)
            }
        }).catch(err => {
            Toast.fail('系统异常')
            reject(err)
        });
    })
}
export default upLoaderImg
