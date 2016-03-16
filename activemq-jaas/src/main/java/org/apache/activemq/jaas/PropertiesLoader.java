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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|PropertiesLoader
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertiesLoader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
name|Map
argument_list|<
name|FileNameKey
argument_list|,
name|ReloadableProperties
argument_list|>
name|staticCache
init|=
operator|new
name|HashMap
argument_list|<
name|FileNameKey
argument_list|,
name|ReloadableProperties
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|debug
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|(
name|Map
name|options
parameter_list|)
block|{
name|debug
operator|=
name|booleanOption
argument_list|(
literal|"debug"
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialized debug"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ReloadableProperties
name|load
parameter_list|(
name|String
name|nameProperty
parameter_list|,
name|String
name|fallbackName
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|ReloadableProperties
name|result
decl_stmt|;
name|FileNameKey
name|key
init|=
operator|new
name|FileNameKey
argument_list|(
name|nameProperty
argument_list|,
name|fallbackName
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|key
operator|.
name|setDebug
argument_list|(
name|debug
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|staticCache
init|)
block|{
name|result
operator|=
name|staticCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|ReloadableProperties
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|staticCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|obtained
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|booleanOption
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|public
class|class
name|FileNameKey
block|{
specifier|final
name|File
name|file
decl_stmt|;
specifier|final
name|String
name|absPath
decl_stmt|;
specifier|final
name|boolean
name|reload
decl_stmt|;
specifier|private
name|boolean
name|decrypt
decl_stmt|;
specifier|private
name|boolean
name|debug
decl_stmt|;
specifier|public
name|FileNameKey
parameter_list|(
name|String
name|nameProperty
parameter_list|,
name|String
name|fallbackName
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|(
name|options
argument_list|)
argument_list|,
name|stringOption
argument_list|(
name|nameProperty
argument_list|,
name|fallbackName
argument_list|,
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|absPath
operator|=
name|file
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|reload
operator|=
name|booleanOption
argument_list|(
literal|"reload"
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|decrypt
operator|=
name|booleanOption
argument_list|(
literal|"decrypt"
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|FileNameKey
operator|&&
name|this
operator|.
name|absPath
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|FileNameKey
operator|)
name|other
operator|)
operator|.
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|absPath
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isReload
parameter_list|()
block|{
return|return
name|reload
return|;
block|}
specifier|public
name|File
name|file
parameter_list|()
block|{
return|return
name|file
return|;
block|}
specifier|public
name|boolean
name|isDecrypt
parameter_list|()
block|{
return|return
name|decrypt
return|;
block|}
specifier|public
name|void
name|setDecrypt
parameter_list|(
name|boolean
name|decrypt
parameter_list|)
block|{
name|this
operator|.
name|decrypt
operator|=
name|decrypt
expr_stmt|;
block|}
specifier|private
name|String
name|stringOption
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|nameDefault
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|Object
name|result
init|=
name|options
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|result
operator|!=
literal|null
condition|?
name|result
operator|.
name|toString
argument_list|()
else|:
name|nameDefault
return|;
block|}
specifier|private
name|File
name|baseDir
parameter_list|(
name|Map
name|options
parameter_list|)
block|{
name|File
name|baseDir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|get
argument_list|(
literal|"baseDir"
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
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
literal|"baseDir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using basedir="
operator|+
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|baseDir
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PropsFile="
operator|+
name|absPath
return|;
block|}
specifier|public
name|void
name|setDebug
parameter_list|(
name|boolean
name|debug
parameter_list|)
block|{
name|this
operator|.
name|debug
operator|=
name|debug
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDebug
parameter_list|()
block|{
return|return
name|debug
return|;
block|}
block|}
comment|/**      * For test-usage only.      */
specifier|public
specifier|static
name|void
name|resetUsersAndGroupsCache
parameter_list|()
block|{
name|staticCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

