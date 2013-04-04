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
name|karaf
operator|.
name|itest
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
name|ActiveMQConnectionFactory
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractJmsFeatureTest
extends|extends
name|AbstractFeatureTest
block|{
specifier|protected
name|String
name|consumeMessage
parameter_list|(
name|String
name|nameAndPayload
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|(
name|AbstractFeatureTest
operator|.
name|USER
argument_list|,
name|AbstractFeatureTest
operator|.
name|PASSWORD
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|nameAndPayload
argument_list|)
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|message
operator|.
name|getText
argument_list|()
return|;
block|}
specifier|protected
name|void
name|produceMessage
parameter_list|(
name|String
name|nameAndPayload
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|(
name|AbstractFeatureTest
operator|.
name|USER
argument_list|,
name|AbstractFeatureTest
operator|.
name|PASSWORD
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|session
operator|.
name|createProducer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|nameAndPayload
argument_list|)
argument_list|)
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|nameAndPayload
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
