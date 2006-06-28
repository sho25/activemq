begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|spi
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|properties
operator|.
name|ReflectionUtil
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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

begin_class
specifier|public
specifier|abstract
class|class
name|ReflectionSPIConnectionFactory
extends|extends
name|ClassLoaderSPIConnectionFactory
block|{
specifier|public
name|ConnectionFactory
name|instantiateConnectionFactory
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
name|factoryClass
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|getClassName
argument_list|()
argument_list|)
decl_stmt|;
name|ConnectionFactory
name|factory
init|=
operator|(
name|ConnectionFactory
operator|)
name|factoryClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|configureConnectionFactory
argument_list|(
name|factory
argument_list|,
name|settings
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|configureConnectionFactory
parameter_list|(
name|ConnectionFactory
name|jmsFactory
parameter_list|,
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|jmsFactory
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|String
name|getClassName
parameter_list|()
function_decl|;
block|}
end_class

end_unit

