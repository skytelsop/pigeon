package com.example.pigeon.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
public class DeviceMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String deviceDetails;
    private String location;
    private Date lastLoggedIn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceMetadata that = (DeviceMetadata) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getDeviceDetails(), that.getDeviceDetails()) &&
                Objects.equals(getLocation(), that.getLocation()) &&
                Objects.equals(getLastLoggedIn(), that.getLastLoggedIn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getDeviceDetails(), getLocation(), getLastLoggedIn());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeviceMetadata{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", deviceDetails='").append(deviceDetails).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", lastLoggedIn=").append(lastLoggedIn);
        sb.append('}');
        return sb.toString();
    }
}
