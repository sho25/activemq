begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|jrms
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|test
operator|.
name|JmsTopicSendReceiveWithTwoConnectionsTest
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JRMSTopicSendReceiveTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsTest
block|{
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setBrokerURL
argument_list|(
literal|"jrms://224.1.2.3:5123"
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

