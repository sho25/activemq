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
name|jndi
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
name|ActiveMQConnectionFactory
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
name|CombinationTestSupport
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
name|command
operator|.
name|ActiveMQDestination
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
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Reference
import|;
end_import

begin_class
specifier|public
class|class
name|ObjectFactoryTest
extends|extends
name|CombinationTestSupport
block|{
specifier|public
name|void
name|testConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create sample connection factory
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setBrokerURL
argument_list|(
literal|"vm://test"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setDisableTimeStampsByDefault
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPassword
argument_list|(
literal|"pass"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUseAsyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUseCompression
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUseRetroactiveConsumer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUserName
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
literal|777
argument_list|)
expr_stmt|;
name|factory
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|factory
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|setBackOffMultiplier
argument_list|(
operator|(
name|short
operator|)
literal|32
argument_list|)
expr_stmt|;
comment|// Create reference
name|Reference
name|ref
init|=
name|JNDIReferenceFactory
operator|.
name|createReference
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|factory
argument_list|)
decl_stmt|;
comment|// Get object created based on reference
name|ActiveMQConnectionFactory
name|temp
decl_stmt|;
name|JNDIReferenceFactory
name|refFactory
init|=
operator|new
name|JNDIReferenceFactory
argument_list|()
decl_stmt|;
name|temp
operator|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|refFactory
operator|.
name|getObjectInstance
argument_list|(
name|ref
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Check settings
name|assertEquals
argument_list|(
name|factory
operator|.
name|isDispatchAsync
argument_list|()
argument_list|,
name|temp
operator|.
name|isDispatchAsync
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getBrokerURL
argument_list|()
argument_list|,
name|temp
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getClientID
argument_list|()
argument_list|,
name|temp
operator|.
name|getClientID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isCopyMessageOnSend
argument_list|()
argument_list|,
name|temp
operator|.
name|isCopyMessageOnSend
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isDisableTimeStampsByDefault
argument_list|()
argument_list|,
name|temp
operator|.
name|isDisableTimeStampsByDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isObjectMessageSerializationDefered
argument_list|()
argument_list|,
name|temp
operator|.
name|isObjectMessageSerializationDefered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isOptimizedMessageDispatch
argument_list|()
argument_list|,
name|temp
operator|.
name|isOptimizedMessageDispatch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getPassword
argument_list|()
argument_list|,
name|temp
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isUseAsyncSend
argument_list|()
argument_list|,
name|temp
operator|.
name|isUseAsyncSend
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isUseCompression
argument_list|()
argument_list|,
name|temp
operator|.
name|isUseCompression
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|isUseRetroactiveConsumer
argument_list|()
argument_list|,
name|temp
operator|.
name|isUseRetroactiveConsumer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getUserName
argument_list|()
argument_list|,
name|temp
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|getQueuePrefetch
argument_list|()
argument_list|,
name|temp
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|getQueuePrefetch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
argument_list|,
name|temp
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|factory
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getBackOffMultiplier
argument_list|()
argument_list|,
name|temp
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getBackOffMultiplier
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDestination
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create sample destination
name|ActiveMQDestination
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|()
decl_stmt|;
name|dest
operator|.
name|setPhysicalName
argument_list|(
literal|"TEST.FOO"
argument_list|)
expr_stmt|;
comment|// Create reference
name|Reference
name|ref
init|=
name|JNDIReferenceFactory
operator|.
name|createReference
argument_list|(
name|dest
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// Get object created based on reference
name|ActiveMQDestination
name|temp
decl_stmt|;
name|JNDIReferenceFactory
name|refFactory
init|=
operator|new
name|JNDIReferenceFactory
argument_list|()
decl_stmt|;
name|temp
operator|=
operator|(
name|ActiveMQDestination
operator|)
name|refFactory
operator|.
name|getObjectInstance
argument_list|(
name|ref
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Check settings
name|assertEquals
argument_list|(
name|dest
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
name|temp
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

