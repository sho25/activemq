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
name|store
operator|.
name|amq
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|ExtendedProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|exception
operator|.
name|ResourceNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|RuntimeServices
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|resource
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|resource
operator|.
name|loader
operator|.
name|FileResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|resource
operator|.
name|loader
operator|.
name|ResourceLoader
import|;
end_import

begin_class
specifier|public
class|class
name|CustomResourceLoader
extends|extends
name|ResourceLoader
block|{
specifier|private
specifier|final
specifier|static
name|ThreadLocal
argument_list|<
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|resourcesTL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|FileResourceLoader
name|fileResourceLoader
init|=
operator|new
name|FileResourceLoader
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|commonInit
parameter_list|(
name|RuntimeServices
name|rs
parameter_list|,
name|ExtendedProperties
name|configuration
parameter_list|)
block|{
name|super
operator|.
name|commonInit
argument_list|(
name|rs
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|fileResourceLoader
operator|.
name|commonInit
argument_list|(
name|rs
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|ExtendedProperties
name|configuration
parameter_list|)
block|{
name|fileResourceLoader
operator|.
name|init
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
comment|/**      */
specifier|public
specifier|synchronized
name|InputStream
name|getResourceStream
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
name|InputStream
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"No template name provided"
argument_list|)
throw|;
block|}
name|String
name|value
init|=
literal|null
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|resources
init|=
name|resourcesTL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|resources
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|this
operator|.
name|fileResourceLoader
operator|.
name|getResourceStream
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|result
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|isSourceModified
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|long
name|getLastModified
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
specifier|static
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getResources
parameter_list|()
block|{
return|return
name|resourcesTL
operator|.
name|get
argument_list|()
return|;
block|}
specifier|static
specifier|public
name|void
name|setResources
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|arg0
parameter_list|)
block|{
name|resourcesTL
operator|.
name|set
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

