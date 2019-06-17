begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jaas
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|CallbackHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_comment
comment|/**  * A LoginModule allowing for SSL certificate based authentication based on  * Distinguished Names (DN) stored in text files. The DNs are parsed using a  * Properties class where each line is either<UserName>=<StringifiedSubjectDN>  * or<UserName>=/<SubjectDNRegExp>/. This class also uses a group definition  * file where each line is<GroupName>=<UserName1>,<UserName2>,etc.  * The user and group files' locations must be specified in the  * org.apache.activemq.jaas.textfiledn.user and  * org.apache.activemq.jaas.textfiledn.group properties respectively.  * NOTE: This class will re-read user and group files for every authentication  * (i.e it does live updates of allowed groups and users).  *  * @author sepandm@gmail.com (Sepand)  */
end_comment

begin_class
specifier|public
class|class
name|TextFileCertificateLoginModule
extends|extends
name|CertificateLoginModule
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER_FILE_PROP_NAME
init|=
literal|"org.apache.activemq.jaas.textfiledn.user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROUP_FILE_PROP_NAME
init|=
literal|"org.apache.activemq.jaas.textfiledn.group"
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|groupsByUser
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|regexpByUser
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|usersByDn
decl_stmt|;
comment|/**      * Performs initialization of file paths. A standard JAAS override.      */
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|,
name|Map
name|sharedState
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|super
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|callbackHandler
argument_list|,
name|sharedState
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|usersByDn
operator|=
name|load
argument_list|(
name|USER_FILE_PROP_NAME
argument_list|,
literal|""
argument_list|,
name|options
argument_list|)
operator|.
name|invertedPropertiesMap
argument_list|()
expr_stmt|;
name|regexpByUser
operator|=
name|load
argument_list|(
name|USER_FILE_PROP_NAME
argument_list|,
literal|""
argument_list|,
name|options
argument_list|)
operator|.
name|regexpPropertiesMap
argument_list|()
expr_stmt|;
name|groupsByUser
operator|=
name|load
argument_list|(
name|GROUP_FILE_PROP_NAME
argument_list|,
literal|""
argument_list|,
name|options
argument_list|)
operator|.
name|invertedPropertiesValuesMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * Overriding to allow DN authorization based on DNs specified in text      * files.      *      * @param certs The certificate the incoming connection provided.      * @return The user's authenticated name or null if unable to authenticate      *         the user.      * @throws LoginException Thrown if unable to find user file or connection      *                 certificate.      */
annotation|@
name|Override
specifier|protected
name|String
name|getUserNameForCertificates
parameter_list|(
specifier|final
name|X509Certificate
index|[]
name|certs
parameter_list|)
throws|throws
name|LoginException
block|{
if|if
condition|(
name|certs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Client certificates not found. Cannot authenticate."
argument_list|)
throw|;
block|}
name|String
name|dn
init|=
name|getDistinguishedName
argument_list|(
name|certs
argument_list|)
decl_stmt|;
return|return
name|usersByDn
operator|.
name|containsKey
argument_list|(
name|dn
argument_list|)
condition|?
name|usersByDn
operator|.
name|get
argument_list|(
name|dn
argument_list|)
else|:
name|getUserByRegexp
argument_list|(
name|dn
argument_list|)
return|;
block|}
comment|/**      * Overriding to allow for group discovery based on text files.      *      * @param username The name of the user being examined. This is the same      *                name returned by getUserNameForCertificates.      * @return A Set of name Strings for groups this user belongs to.      * @throws LoginException Thrown if unable to find group definition file.      */
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getUserGroups
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|LoginException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|userGroups
init|=
name|groupsByUser
operator|.
name|get
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|userGroups
operator|==
literal|null
condition|)
block|{
name|userGroups
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
return|return
name|userGroups
return|;
block|}
specifier|private
specifier|synchronized
name|String
name|getUserByRegexp
parameter_list|(
name|String
name|dn
parameter_list|)
block|{
name|String
name|name
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|val
range|:
name|regexpByUser
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|val
operator|.
name|getValue
argument_list|()
operator|.
name|matcher
argument_list|(
name|dn
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|name
operator|=
name|val
operator|.
name|getKey
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|usersByDn
operator|.
name|put
argument_list|(
name|dn
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

