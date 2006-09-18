begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
comment|/**  * A LoginModule allowing for SSL certificate based authentication based on Distinguished Names (DN) stored in text  *      files.  *        * The DNs are parsed using a Properties class where each line is<user_name>=<user_DN>.  * This class also uses a group definition file where each line is<group_name>=<user_name_1>,<user_name_2>,etc.  * The user and group files' locations must be specified in the org.apache.activemq.jaas.textfiledn.user and  *      org.apache.activemq.jaas.textfiledn.user properties respectively.  *   * NOTE: This class will re-read user and group files for every authentication (i.e it does live updates of allowed  *      groups and users).  *   * @author sepandm@gmail.com (Sepand)  */
end_comment

begin_class
specifier|public
class|class
name|TextFileCertificateLoginModule
extends|extends
name|CertificateLoginModule
block|{
specifier|private
specifier|final
name|String
name|USER_FILE
init|=
literal|"org.apache.activemq.jaas.textfiledn.user"
decl_stmt|;
specifier|private
specifier|final
name|String
name|GROUP_FILE
init|=
literal|"org.apache.activemq.jaas.textfiledn.group"
decl_stmt|;
specifier|private
name|File
name|baseDir
decl_stmt|;
specifier|private
name|String
name|usersFilePathname
decl_stmt|;
specifier|private
name|String
name|groupsFilePathname
decl_stmt|;
comment|/**      * Performs initialization of file paths.      *       * A standard JAAS override.      */
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
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
argument_list|)
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
name|usersFilePathname
operator|=
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|USER_FILE
argument_list|)
operator|+
literal|""
expr_stmt|;
name|groupsFilePathname
operator|=
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|GROUP_FILE
argument_list|)
operator|+
literal|""
expr_stmt|;
block|}
comment|/**      * Overriding to allow DN authorization based on DNs specified in text files.      *        * @param certs The certificate the incoming connection provided.      * @return The user's authenticated name or null if unable to authenticate the user.      * @throws LoginException Thrown if unable to find user file or connection certificate.       */
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
name|File
name|usersFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|usersFilePathname
argument_list|)
decl_stmt|;
name|Properties
name|users
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|users
operator|.
name|load
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileInputStream
argument_list|(
name|usersFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Unable to load user properties file "
operator|+
name|usersFile
argument_list|)
throw|;
block|}
name|String
name|dn
init|=
name|certs
index|[
literal|0
index|]
operator|.
name|getSubjectDN
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
name|vals
init|=
name|users
operator|.
name|elements
argument_list|()
init|,
name|keys
init|=
name|users
operator|.
name|keys
argument_list|()
init|;
name|vals
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|vals
operator|.
name|nextElement
argument_list|()
operator|)
operator|.
name|equals
argument_list|(
name|dn
argument_list|)
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|keys
operator|.
name|nextElement
argument_list|()
return|;
block|}
else|else
block|{
name|keys
operator|.
name|nextElement
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Overriding to allow for group discovery based on text files.      *       * @param username The name of the user being examined. This is the same name returned by      *      getUserNameForCertificates.      * @return A Set of name Strings for groups this user belongs to.      * @throws LoginException Thrown if unable to find group definition file.      */
specifier|protected
name|Set
name|getUserGroups
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|LoginException
block|{
name|File
name|groupsFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|groupsFilePathname
argument_list|)
decl_stmt|;
name|Properties
name|groups
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|groups
operator|.
name|load
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileInputStream
argument_list|(
name|groupsFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Unable to load group properties file "
operator|+
name|groupsFile
argument_list|)
throw|;
block|}
name|Set
name|userGroups
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
name|enumeration
init|=
name|groups
operator|.
name|keys
argument_list|()
init|;
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|groupName
init|=
operator|(
name|String
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
index|[]
name|userList
init|=
operator|(
name|groups
operator|.
name|getProperty
argument_list|(
name|groupName
argument_list|)
operator|+
literal|""
operator|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|userList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|username
operator|.
name|equals
argument_list|(
name|userList
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|userGroups
operator|.
name|add
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|userGroups
return|;
block|}
block|}
end_class

end_unit

