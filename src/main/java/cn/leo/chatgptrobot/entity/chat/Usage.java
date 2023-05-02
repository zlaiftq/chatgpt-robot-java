package cn.leo.chatgptrobot.entity.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Usage
 */
@Data
public class Usage implements Serializable {

    @JsonProperty("prompt_tokens")
    private long promptTokens;

    @JsonProperty("completion_tokens")
    private long completionTokens;

    @JsonProperty("total_tokens")
    private long totalTokens;

}
