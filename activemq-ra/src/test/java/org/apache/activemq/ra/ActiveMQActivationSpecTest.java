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
name|java
operator|.
name|beans
operator|.
name|IntrospectionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
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
name|Topic
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
name|InvalidPropertyException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQActivationSpecTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION
init|=
literal|"defaultQueue"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_TYPE
init|=
name|Queue
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_STRING
init|=
literal|"   "
decl_stmt|;
specifier|private
name|ActiveMQActivationSpec
name|activationSpec
decl_stmt|;
specifier|private
name|PropertyDescriptor
name|destinationProperty
decl_stmt|;
specifier|private
name|PropertyDescriptor
name|destinationTypeProperty
decl_stmt|;
specifier|private
name|PropertyDescriptor
name|acknowledgeModeProperty
decl_stmt|;
specifier|private
name|PropertyDescriptor
name|subscriptionDurabilityProperty
decl_stmt|;
specifier|private
name|PropertyDescriptor
name|clientIdProperty
decl_stmt|;
specifier|private
name|PropertyDescriptor
name|subscriptionNameProperty
decl_stmt|;
specifier|public
name|ActiveMQActivationSpecTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|activationSpec
operator|=
operator|new
name|ActiveMQActivationSpec
argument_list|()
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
name|DESTINATION
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|DESTINATION_TYPE
argument_list|)
expr_stmt|;
name|destinationProperty
operator|=
operator|new
name|PropertyDescriptor
argument_list|(
literal|"destination"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
expr_stmt|;
name|destinationTypeProperty
operator|=
operator|new
name|PropertyDescriptor
argument_list|(
literal|"destinationType"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
expr_stmt|;
name|acknowledgeModeProperty
operator|=
operator|new
name|PropertyDescriptor
argument_list|(
literal|"acknowledgeMode"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
expr_stmt|;
name|subscriptionDurabilityProperty
operator|=
operator|new
name|PropertyDescriptor
argument_list|(
literal|"subscriptionDurability"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
expr_stmt|;
name|clientIdProperty
operator|=
operator|new
name|PropertyDescriptor
argument_list|(
literal|"clientId"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
expr_stmt|;
name|subscriptionNameProperty
operator|=
operator|new
name|PropertyDescriptor
argument_list|(
literal|"subscriptionName"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDefaultContructionValidation
parameter_list|()
throws|throws
name|IntrospectionException
block|{
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|destinationTypeProperty
block|,
name|destinationProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
operator|new
name|ActiveMQActivationSpec
argument_list|()
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMinimalSettings
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|DESTINATION
argument_list|,
name|activationSpec
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DESTINATION_TYPE
argument_list|,
name|activationSpec
operator|.
name|getDestinationType
argument_list|()
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoDestinationTypeFailure
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|destinationTypeProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidDestinationTypeFailure
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|destinationTypeProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQueueDestinationType
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Queue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testTopicDestinationType
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Topic
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSuccessfulCreateQueueDestination
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Queue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
name|DESTINATION
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|activationSpec
operator|.
name|createDestination
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"ActiveMQDestination not created"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Physical name not the same"
argument_list|,
name|activationSpec
operator|.
name|getDestination
argument_list|()
argument_list|,
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Destination is not a Queue"
argument_list|,
name|destination
operator|instanceof
name|Queue
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSuccessfulCreateTopicDestination
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Topic
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
name|DESTINATION
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|activationSpec
operator|.
name|createDestination
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"ActiveMQDestination not created"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Physical name not the same"
argument_list|,
name|activationSpec
operator|.
name|getDestination
argument_list|()
argument_list|,
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Destination is not a Topic"
argument_list|,
name|destination
operator|instanceof
name|Topic
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateDestinationIncorrectType
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
name|DESTINATION
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|activationSpec
operator|.
name|createDestination
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"ActiveMQDestination should not have been created"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateDestinationIncorrectDestinationName
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Topic
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|activationSpec
operator|.
name|createDestination
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"ActiveMQDestination should not have been created"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
comment|//----------- acknowledgeMode tests
specifier|public
name|void
name|testDefaultAcknowledgeModeSetCorrectly
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Incorrect default value"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|AUTO_ACKNOWLEDGE_MODE
argument_list|,
name|activationSpec
operator|.
name|getAcknowledgeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect default value"
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|,
name|activationSpec
operator|.
name|getAcknowledgeModeForSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidAcknowledgeMode
parameter_list|()
block|{
name|activationSpec
operator|.
name|setAcknowledgeMode
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|acknowledgeModeProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect acknowledge mode"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|INVALID_ACKNOWLEDGE_MODE
argument_list|,
name|activationSpec
operator|.
name|getAcknowledgeModeForSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNoAcknowledgeMode
parameter_list|()
block|{
name|activationSpec
operator|.
name|setAcknowledgeMode
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|acknowledgeModeProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect acknowledge mode"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|INVALID_ACKNOWLEDGE_MODE
argument_list|,
name|activationSpec
operator|.
name|getAcknowledgeModeForSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSettingAutoAcknowledgeMode
parameter_list|()
block|{
name|activationSpec
operator|.
name|setAcknowledgeMode
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|AUTO_ACKNOWLEDGE_MODE
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect acknowledge mode"
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|,
name|activationSpec
operator|.
name|getAcknowledgeModeForSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSettingDupsOkAcknowledgeMode
parameter_list|()
block|{
name|activationSpec
operator|.
name|setAcknowledgeMode
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|DUPS_OK_ACKNOWLEDGE_MODE
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect acknowledge mode"
argument_list|,
name|Session
operator|.
name|DUPS_OK_ACKNOWLEDGE
argument_list|,
name|activationSpec
operator|.
name|getAcknowledgeModeForSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//----------- subscriptionDurability tests
specifier|public
name|void
name|testDefaultSubscriptionDurabilitySetCorrectly
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Incorrect default value"
argument_list|,
name|ActiveMQActivationSpec
operator|.
name|NON_DURABLE_SUBSCRIPTION
argument_list|,
name|activationSpec
operator|.
name|getSubscriptionDurability
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidSubscriptionDurability
parameter_list|()
block|{
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|subscriptionDurabilityProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNullSubscriptionDurability
parameter_list|()
block|{
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|subscriptionDurabilityProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSettingNonDurableSubscriptionDurability
parameter_list|()
block|{
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|NON_DURABLE_SUBSCRIPTION
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
block|}
comment|//----------- durable subscriber tests
specifier|public
name|void
name|testValidDurableSubscriber
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Topic
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|DURABLE_SUBSCRIPTION
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setClientId
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionName
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|assertActivationSpecValid
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|activationSpec
operator|.
name|isDurableSubscription
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDurableSubscriberWithQueueDestinationTypeFailure
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Queue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|DURABLE_SUBSCRIPTION
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setClientId
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionName
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|subscriptionDurabilityProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDurableSubscriberNoClientIdNoSubscriptionNameFailure
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Topic
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|DURABLE_SUBSCRIPTION
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setClientId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|activationSpec
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionName
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|activationSpec
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|clientIdProperty
block|,
name|subscriptionNameProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDurableSubscriberEmptyClientIdEmptySubscriptionNameFailure
parameter_list|()
block|{
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Topic
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionDurability
argument_list|(
name|ActiveMQActivationSpec
operator|.
name|DURABLE_SUBSCRIPTION
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setClientId
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|activationSpec
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionName
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|activationSpec
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyDescriptor
index|[]
name|expected
init|=
block|{
name|clientIdProperty
block|,
name|subscriptionNameProperty
block|}
decl_stmt|;
name|assertActivationSpecInvalid
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetEmptyStringButGetNullValue
parameter_list|()
block|{
name|ActiveMQActivationSpec
name|activationSpec
init|=
operator|new
name|ActiveMQActivationSpec
argument_list|()
decl_stmt|;
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getDestinationType
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setMessageSelector
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getMessageSelector
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setUserName
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setPassword
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setClientId
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setSubscriptionName
argument_list|(
name|EMPTY_STRING
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Property not null"
argument_list|,
name|activationSpec
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//----------- helper methods
specifier|private
name|void
name|assertActivationSpecValid
parameter_list|()
block|{
try|try
block|{
name|activationSpec
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidPropertyException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"InvalidPropertyException should not be thrown"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertActivationSpecInvalid
parameter_list|(
name|PropertyDescriptor
index|[]
name|expected
parameter_list|)
block|{
name|assertActivationSpecInvalid
argument_list|(
name|activationSpec
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertActivationSpecInvalid
parameter_list|(
name|ActiveMQActivationSpec
name|testActivationSpec
parameter_list|,
name|PropertyDescriptor
index|[]
name|expected
parameter_list|)
block|{
try|try
block|{
name|testActivationSpec
operator|.
name|validate
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"InvalidPropertyException should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidPropertyException
name|e
parameter_list|)
block|{
name|PropertyDescriptor
index|[]
name|actual
init|=
name|e
operator|.
name|getInvalidPropertyDescriptors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertEquals
parameter_list|(
name|PropertyDescriptor
index|[]
name|expected
parameter_list|,
name|PropertyDescriptor
index|[]
name|actual
parameter_list|)
block|{
comment|/*         * This is kind of ugly.  I originally created two HashSets and did an assertEquals(set1, set2)          * but because of a bug in the PropertyDescriptor class, it incorrectly fails.  The problem is that the          * PropertyDescriptor class implements the equals() method but not the hashCode() method and almost all         * of the java collection classes use hashCode() for testing equality.  The one exception I found was         * the ArrayList class which uses equals() for testing equality.  Since Arrays.asList(...) returns an         * ArrayList, I use it below.  Yes, ugly ... I know.         *         * see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4634390         */
name|assertNotNull
argument_list|(
literal|"No PropertyDescriptors returned"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PropertyDescriptor array size is incorrect "
argument_list|,
name|expected
operator|.
name|length
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PropertyDescriptor
argument_list|>
name|expectedList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PropertyDescriptor
argument_list|>
name|actualList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|actual
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Incorrect PropertyDescriptors returned"
argument_list|,
name|expectedList
operator|.
name|containsAll
argument_list|(
name|actualList
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSelfEquality
parameter_list|()
block|{
name|assertEquality
argument_list|(
name|activationSpec
argument_list|,
name|activationSpec
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSamePropertiesButNotEqual
parameter_list|()
block|{
name|assertNonEquality
argument_list|(
operator|new
name|ActiveMQActivationSpec
argument_list|()
argument_list|,
operator|new
name|ActiveMQActivationSpec
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertEquality
parameter_list|(
name|ActiveMQActivationSpec
name|leftSpec
parameter_list|,
name|ActiveMQActivationSpec
name|rightSpec
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"ActiveMQActivationSpecs are not equal"
argument_list|,
name|leftSpec
operator|.
name|equals
argument_list|(
name|rightSpec
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ActiveMQActivationSpecs are not equal"
argument_list|,
name|rightSpec
operator|.
name|equals
argument_list|(
name|leftSpec
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"HashCodes are not equal"
argument_list|,
name|leftSpec
operator|.
name|hashCode
argument_list|()
operator|==
name|rightSpec
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertNonEquality
parameter_list|(
name|ActiveMQActivationSpec
name|leftSpec
parameter_list|,
name|ActiveMQActivationSpec
name|rightSpec
parameter_list|)
block|{
name|assertFalse
argument_list|(
literal|"ActiveMQActivationSpecs are equal"
argument_list|,
name|leftSpec
operator|.
name|equals
argument_list|(
name|rightSpec
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"ActiveMQActivationSpecs are equal"
argument_list|,
name|rightSpec
operator|.
name|equals
argument_list|(
name|leftSpec
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"HashCodes are equal"
argument_list|,
name|leftSpec
operator|.
name|hashCode
argument_list|()
operator|==
name|rightSpec
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

