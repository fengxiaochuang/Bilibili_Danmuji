package xyz.acproject.danmuji.service.impl;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.nlp.v20190408.NlpClient;
import com.tencentcloudapi.nlp.v20190408.models.ChatBotRequest;
import com.tencentcloudapi.nlp.v20190408.models.ChatBotResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import xyz.acproject.danmuji.entity.other.Weather;
import xyz.acproject.danmuji.http.HttpOtherData;
import xyz.acproject.danmuji.service.ApiService;
import xyz.acproject.danmuji.utils.XmlUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ApiServiceImpl implements ApiService {

    private Logger logger = Logger.getLogger("ApiServiceImpl");

    @Override
    public Weather getWeather(String city, Short day) {
        Weather weather = null;
        Map<String, List<Weather>> weatherMaps = HttpOtherData.httpGetweather(city);
        if (null != weatherMaps) {
            List<Weather> oldWeathers = weatherMaps.get("old");
            List<Weather> newWeathers = weatherMaps.get("new");
            switch (day) {
                case -1:
                    if (null != oldWeathers && oldWeathers.size() > 0)
                        weather = oldWeathers.get(0);
                    break;
                case 0:
                    if (null != newWeathers && newWeathers.size() > 0)
                        weather = newWeathers.get(0);
                    break;
                case 1:
                    if (null != newWeathers && newWeathers.size() > 0)
                        weather = newWeathers.get(1);
                    break;
                case 2:
                    if (null != newWeathers && newWeathers.size() > 0)
                        weather = newWeathers.get(2);
                    break;
                case 3:
                    if (null != newWeathers && newWeathers.size() > 0)
                        weather = newWeathers.get(3);
                    break;
                case 4:
                    if (null != newWeathers && newWeathers.size() > 0)
                        weather = newWeathers.get(4);
                    break;
                default:
                    break;
            }
        }
        if (null != weather) {
            weather.setFl(XmlUtils.getData(weather.getFl(), String.class));
        }
        return weather;
    }

    @Override
    public String chat(String question, String userId) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential("", "");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("nlp.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            ChatBotRequest req = new ChatBotRequest();
            req.setQuery(question);
            if (StringUtils.isNotBlank(userId)) {
                req.setOpenId(userId);
            }
            // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
            ChatBotResponse resp = client.ChatBot(req);
            return resp.getReply();
        } catch (TencentCloudSDKException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        return null;
    }
}
