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
name|transport
package|;
end_package

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
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|ServerConnector
import|;
end_import

begin_class
specifier|public
class|class
name|SocketConnectorFactory
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
decl_stmt|;
specifier|public
name|Connector
name|createConnector
parameter_list|(
name|Server
name|server
parameter_list|)
throws|throws
name|Exception
block|{
name|ServerConnector
name|connector
init|=
operator|new
name|ServerConnector
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|server
operator|.
name|setStopTimeout
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setStopTimeout
argument_list|(
literal|500
argument_list|)
expr_stmt|;
if|if
condition|(
name|transportOptions
operator|!=
literal|null
condition|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|connector
argument_list|,
name|transportOptions
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|connector
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getTransportOptions
parameter_list|()
block|{
return|return
name|transportOptions
return|;
block|}
specifier|public
name|void
name|setTransportOptions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
parameter_list|)
block|{
name|this
operator|.
name|transportOptions
operator|=
name|transportOptions
expr_stmt|;
block|}
block|}
end_class

end_unit

