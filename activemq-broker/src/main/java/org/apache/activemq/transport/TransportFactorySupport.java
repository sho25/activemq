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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|BrokerServiceAware
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
name|broker
operator|.
name|SslContext
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
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|TransportFactorySupport
block|{
specifier|public
specifier|static
name|TransportServer
name|bind
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|TransportFactory
name|tf
init|=
name|TransportFactory
operator|.
name|findTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerService
operator|!=
literal|null
operator|&&
name|tf
operator|instanceof
name|BrokerServiceAware
condition|)
block|{
operator|(
operator|(
name|BrokerServiceAware
operator|)
name|tf
operator|)
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|SslContext
operator|.
name|setCurrentSslContext
argument_list|(
name|brokerService
operator|.
name|getSslContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|tf
operator|.
name|doBind
argument_list|(
name|location
argument_list|)
return|;
block|}
finally|finally
block|{
name|SslContext
operator|.
name|setCurrentSslContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

