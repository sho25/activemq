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
name|ra
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
name|resource
operator|.
name|spi
operator|.
name|BootstrapContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpointFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Mock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|cglib
operator|.
name|MockObjectTestCase
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQAsfEndpointWorkerTest
extends|extends
name|MockObjectTestCase
block|{
specifier|private
name|Mock
name|mockResourceAdapter
decl_stmt|;
specifier|private
name|Mock
name|mockActivationKey
decl_stmt|;
specifier|private
name|Mock
name|mockEndpointFactory
decl_stmt|;
specifier|private
name|Mock
name|mockBootstrapContext
decl_stmt|;
specifier|private
name|ActiveMQActivationSpec
name|stubActivationSpec
decl_stmt|;
comment|//    private Mock mockConnection;
specifier|public
name|ActiveMQAsfEndpointWorkerTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTopicSubscriberDurableNoDups
parameter_list|()
throws|throws
name|Exception
block|{
comment|//         Constraint[] args = {isA(Topic.class),
comment|//            eq(stubActivationSpec.getSubscriptionId()),
comment|//            NULL,
comment|//            ANYTHING,
comment|//            ANYTHING};
comment|//         mockConnection.expects(once()).method("createDurableConnectionConsumer").with(args)
comment|//            .will(returnValue(null));
comment|//         worker.start();
comment|//         verifyMocks();
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setupStubs
argument_list|()
expr_stmt|;
name|setupMocks
argument_list|()
expr_stmt|;
name|setupEndpointWorker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setupStubs
parameter_list|()
block|{
name|stubActivationSpec
operator|=
operator|new
name|ActiveMQActivationSpec
argument_list|()
expr_stmt|;
name|stubActivationSpec
operator|.
name|setDestination
argument_list|(
literal|"some.topic"
argument_list|)
expr_stmt|;
name|stubActivationSpec
operator|.
name|setDestinationType
argument_list|(
literal|"javax.jms.Topic"
argument_list|)
expr_stmt|;
name|stubActivationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|DURABLE_SUBSCRIPTION
argument_list|)
expr_stmt|;
name|stubActivationSpec
operator|.
name|setClientId
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|stubActivationSpec
operator|.
name|setSubscriptionName
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupMocks
parameter_list|()
block|{
name|mockResourceAdapter
operator|=
name|mock
argument_list|(
name|ActiveMQResourceAdapter
operator|.
name|class
argument_list|)
expr_stmt|;
name|mockActivationKey
operator|=
name|mock
argument_list|(
name|ActiveMQEndpointActivationKey
operator|.
name|class
argument_list|)
expr_stmt|;
name|mockEndpointFactory
operator|=
name|mock
argument_list|(
name|MessageEndpointFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|mockBootstrapContext
operator|=
name|mock
argument_list|(
name|BootstrapContext
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//        mockConnection = mock(Connection.class);
name|mockActivationKey
operator|.
name|expects
argument_list|(
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"getMessageEndpointFactory"
argument_list|)
operator|.
name|will
argument_list|(
name|returnValue
argument_list|(
operator|(
name|MessageEndpointFactory
operator|)
name|mockEndpointFactory
operator|.
name|proxy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mockActivationKey
operator|.
name|expects
argument_list|(
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"getActivationSpec"
argument_list|)
operator|.
name|will
argument_list|(
name|returnValue
argument_list|(
name|stubActivationSpec
argument_list|)
argument_list|)
expr_stmt|;
name|mockResourceAdapter
operator|.
name|expects
argument_list|(
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"getBootstrapContext"
argument_list|)
operator|.
name|will
argument_list|(
name|returnValue
argument_list|(
operator|(
name|BootstrapContext
operator|)
name|mockBootstrapContext
operator|.
name|proxy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mockBootstrapContext
operator|.
name|expects
argument_list|(
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"getWorkManager"
argument_list|)
operator|.
name|will
argument_list|(
name|returnValue
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|isTransactedResult
init|=
literal|true
decl_stmt|;
name|setupIsTransacted
argument_list|(
name|isTransactedResult
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupIsTransacted
parameter_list|(
specifier|final
name|boolean
name|transactedResult
parameter_list|)
block|{
name|mockEndpointFactory
operator|.
name|expects
argument_list|(
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"isDeliveryTransacted"
argument_list|)
operator|.
name|with
argument_list|(
name|ANYTHING
argument_list|)
operator|.
name|will
argument_list|(
name|returnValue
argument_list|(
name|transactedResult
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupEndpointWorker
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|ActiveMQEndpointWorker
argument_list|(
operator|(
name|ActiveMQResourceAdapter
operator|)
name|mockResourceAdapter
operator|.
name|proxy
argument_list|()
argument_list|,
operator|(
name|ActiveMQEndpointActivationKey
operator|)
name|mockActivationKey
operator|.
name|proxy
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//    private void verifyMocks() {
comment|//        mockResourceAdapter.verify();
comment|//        mockActivationKey.verify();
comment|//        mockEndpointFactory.verify();
comment|//        mockBootstrapContext.verify();
comment|//        mockConnection.verify();
comment|//    }
block|}
end_class

end_unit

