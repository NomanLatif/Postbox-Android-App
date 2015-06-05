package com.ericsson.postbox.entity;

/**
 * Created by Noman on 12/6/2014.
 */
public class User
{
    private long myId;
    private String myPassword;
    private String myFullName;
    private String myEmail;
    private String myStatus;
    private String myStatusReason;
    private Double myLongitude;
    private Double myLatitude;
    private String myGcmRegisterationId;

    private User(UserBuilder builder)
    {
        myId = builder.myId;
        myPassword = builder.myPassword;
        myFullName = builder.myFullName;
        myEmail = builder.myEmail;
        myStatus = builder.myStatus;
        myStatusReason = builder.myStatusReason;
        myLongitude = builder.myLongitude;
        myLatitude = builder.myLatitude;
        myGcmRegisterationId = builder.myGcmRegistrationId;
    }

    public long getId()
    {
        return myId;
    }
    public String getPassword()
    {
        return myPassword;
    }

    public String getName()
    {
        return myFullName;
    }

    public String getEmail()
    {
        return myEmail;
    }

    public String getStatus()
    {
        return myStatus;
    }

    public String getStatusReason()
    {
        return myStatusReason;
    }

    public Double getLongitude()
    {
        return myLongitude;
    }

    public Double getLatitude()
    {
        return myLatitude;
    }

    public String getGcmRegistationId()
    {
        return myGcmRegisterationId;
    }

    public static class UserBuilder
    {
        private long myId;
        private String myPassword;
        private String myFullName;
        private String myEmail;
        private String myStatus;
        private String myStatusReason;
        private Double myLongitude;
        private Double myLatitude;
        private String myGcmRegistrationId;

        public UserBuilder withId(long id)
        {
            this.myId = id;
            return this;
        }

        public UserBuilder withPassword(String password)
        {
            this.myPassword = password;
            return this;
        }

        public UserBuilder withFullName(String fullName)
        {
            this.myFullName = fullName;
            return this;
        }

        public UserBuilder withEmail(String email)
        {
            this.myEmail = email;
            return this;
        }

        public UserBuilder withStatus(String status)
        {
            this.myStatus = status;
            return this;
        }

        public UserBuilder withStatusReason(String statusReason)
        {
            this.myStatusReason = statusReason;
            return this;
        }

        public UserBuilder withLongitude(Double longitude)
        {
            this.myLongitude = longitude;
            return this;
        }

        public UserBuilder withLatitude(Double latitude)
        {
            this.myLatitude = latitude;
            return this;
        }

        public User build()
        {
            return new User(this);
        }

        public UserBuilder withGcmRegisterationId(String gcmRegId)
        {
            this.myGcmRegistrationId = gcmRegId;
            return this;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        User user = (User) o;

        if (myId != user.myId)
        {
            return false;
        }
        if (myEmail != null ? !myEmail.equals(user.myEmail) : user.myEmail != null)
        {
            return false;
        }
        if (myFullName != null ? !myFullName.equals(user.myFullName) : user.myFullName != null)
        {
            return false;
        }
        if (myLatitude != null ? !myLatitude.equals(user.myLatitude) : user.myLatitude != null)
        {
            return false;
        }
        if (myLongitude != null ? !myLongitude.equals(user.myLongitude) : user.myLongitude != null)
        {
            return false;
        }
        if (myPassword != null ? !myPassword.equals(user.myPassword) : user.myPassword != null)
        {
            return false;
        }
        if (myStatus != null ? !myStatus.equals(user.myStatus) : user.myStatus != null)
        {
            return false;
        }
        return !(myStatusReason != null ? !myStatusReason.equals(user.myStatusReason) : user.myStatusReason != null);

    }

    @Override
    public int hashCode()
    {
        int result = (int) (myId ^ (myId >>> 32));
        result = 31 * result + (myPassword != null ? myPassword.hashCode() : 0);
        result = 31 * result + (myFullName != null ? myFullName.hashCode() : 0);
        result = 31 * result + (myEmail != null ? myEmail.hashCode() : 0);
        result = 31 * result + (myStatus != null ? myStatus.hashCode() : 0);
        result = 31 * result + (myStatusReason != null ? myStatusReason.hashCode() : 0);
        result = 31 * result + (myLongitude != null ? myLongitude.hashCode() : 0);
        result = 31 * result + (myLatitude != null ? myLatitude.hashCode() : 0);
        return result;
    }
}
