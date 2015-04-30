/**
 * CmpUser.java
 */
package com.jifa.test.pojo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.bstek.bdf2.core.business.AbstractUser;
import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.business.IPosition;
import com.bstek.bdf2.core.model.Group;
import com.bstek.bdf2.core.model.Role;
import com.jifa.core.pojo.IPojo;

/**
 *
 * @Description: 替换自带的user
 * @author june
 * @date 2014-2-12 上午9:35:45
 * 
 */
@Entity
@Table(name=User.DBNAME)
public class User extends AbstractUser implements IPojo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4252058125000656700L;
	public static final String DBNAME = "bdf2_user";
	@Id
	@Column(name="USERNAME_",length=60)
	private String username;	
	@Column(name="PASSWORD_",length=70,nullable=false)
	private String password;
	@Column(name="SALT_",length=10,nullable=false)
	private String salt;
	@Column(name="CNAME_",length=60,nullable=false)
	private String cname;
	@Column(name="ENAME_",length=60)
	private String ename;
	@Column(name="MALE_",nullable=false)
	private boolean male=true;
	@Column(name="ENABLED_",nullable=false)
	private boolean enabled=true;
	@Column(name="ADMINISTRATOR_",nullable=false)
	private boolean administrator=false;
	
	@Column(name="MOBILE_",length=20)
	private String mobile;
	
	@Column(name="EMAIL_",length=60)
	private String email;
	@Column(name="COMPANY_ID_",length=60,nullable=false)
	private String companyId;


	
	@Transient
	private List<IPosition> positions;
	
	@Transient
	private List<IDept> depts;
	
	@Transient
	private List<Group> groups;
	
	@Transient
	private List<Role> roles;
	
	public User(){}
	/**
	 * @param username
	 */
	public User(String username) {
		this.username = username;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}
	/**
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}
	/**
	 * @return the cname
	 */
	public String getCname() {
		return cname;
	}
	/**
	 * @param cname the cname to set
	 */
	public void setCname(String cname) {
		this.cname = cname;
	}
	/**
	 * @return the ename
	 */
	public String getEname() {
		return ename;
	}
	/**
	 * @param ename the ename to set
	 */
	public void setEname(String ename) {
		this.ename = ename;
	}
	/**
	 * @return the male
	 */
	public boolean isMale() {
		return male;
	}
	/**
	 * @param male the male to set
	 */
	public void setMale(boolean male) {
		this.male = male;
	}
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * @return the administrator
	 */
	public boolean isAdministrator() {
		return administrator;
	}
	/**
	 * @param administrator the administrator to set
	 */
	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the companyId
	 */
	public String getCompanyId() {
		return companyId;
	}
	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	/**
	 * @return the positions
	 */
	public List<IPosition> getPositions() {
		return positions;
	}
	/**
	 * @param positions the positions to set
	 */
	public void setPositions(List<IPosition> positions) {
		this.positions = positions;
	}
	/**
	 * @return the depts
	 */
	public List<IDept> getDepts() {
		return depts;
	}
	/**
	 * @param depts the depts to set
	 */
	public void setDepts(List<IDept> depts) {
		this.depts = depts;
	}
	/**
	 * @return the groups
	 */
	public List<Group> getGroups() {
		return groups;
	}
	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}
	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	/* (non-Javadoc)
	 * @see com.bstek.bdf2.core.business.IUser#getMetadata()
	 */
	public Map<String, Object> getMetadata() {

		return null;
	}

}
