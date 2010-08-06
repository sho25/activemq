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
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_comment
comment|/*  * allow an XA session to be used as an auto ack session when no XA transaction  * https://issues.apache.org/activemq/browse/AMQ-2659  */
end_comment

begin_class
specifier|public
class|class
name|JMSXAConsumerTest
extends|extends
name|JMSConsumerTest
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|JMSXAConsumerTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
block|}
comment|// some tests use transactions, these will not work unless an XA transaction is in place
comment|// slip these
specifier|public
name|void
name|testPrefetch1MessageNotDispatched
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|testRedispatchOfUncommittedTx
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|testRedispatchOfRolledbackTx
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|testMessageListenerOnMessageCloseUnackedWithPrefetch1StayInQueue
parameter_list|()
throws|throws
name|Exception
block|{     }
block|}
end_class

end_unit

