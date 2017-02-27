package eu.luminis.elastic.search.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.luminis.elastic.document.response.Shards;

public class CountResponse {
    @JsonProperty(value = "_shards")
    private Shards shards;

    private long count;

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
