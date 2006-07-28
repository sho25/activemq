begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ra
operator|.
name|ActiveMQEndpointWorker
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
name|ra
operator|.
name|InvalidMessageEndpointException
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
name|ra
operator|.
name|MessageEndpointProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|MockObjectTestCase
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
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|MessageEndpoint
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_class
specifier|public
class|class
name|MessageEndpointProxyTest
extends|extends
name|MockObjectTestCase
block|{
specifier|private
name|Mock
name|mockEndpoint
decl_stmt|;
specifier|private
name|Mock
name|stubMessage
decl_stmt|;
specifier|private
name|MessageEndpointProxy
name|endpointProxy
decl_stmt|;
specifier|public
name|MessageEndpointProxyTest
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
block|{
name|mockEndpoint
operator|=
operator|new
name|Mock
argument_list|(
name|EndpointAndListener
operator|.
name|class
argument_list|)
expr_stmt|;
name|stubMessage
operator|=
operator|new
name|Mock
argument_list|(
name|Message
operator|.
name|class
argument_list|)
expr_stmt|;
name|endpointProxy
operator|=
operator|new
name|MessageEndpointProxy
argument_list|(
operator|(
name|MessageEndpoint
operator|)
name|mockEndpoint
operator|.
name|proxy
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidConstruction
parameter_list|()
block|{
name|Mock
name|mockEndpoint
init|=
operator|new
name|Mock
argument_list|(
name|MessageEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|MessageEndpointProxy
name|proxy
init|=
operator|new
name|MessageEndpointProxy
argument_list|(
operator|(
name|MessageEndpoint
operator|)
name|mockEndpoint
operator|.
name|proxy
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"An exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSuccessfulCallSequence
parameter_list|()
block|{
name|setupBeforeDeliverySuccessful
argument_list|()
expr_stmt|;
name|setupOnMessageSuccessful
argument_list|()
expr_stmt|;
name|setupAfterDeliverySuccessful
argument_list|()
expr_stmt|;
name|doBeforeDeliveryExpectSuccess
argument_list|()
expr_stmt|;
name|doOnMessageExpectSuccess
argument_list|()
expr_stmt|;
name|doAfterDeliveryExpectSuccess
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testBeforeDeliveryFailure
parameter_list|()
block|{
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"beforeDelivery"
argument_list|)
operator|.
name|with
argument_list|(
name|isA
argument_list|(
name|Method
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|will
argument_list|(
name|throwException
argument_list|(
operator|new
name|ResourceException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|never
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"onMessage"
argument_list|)
expr_stmt|;
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|never
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"afterDelivery"
argument_list|)
expr_stmt|;
name|setupExpectRelease
argument_list|()
expr_stmt|;
try|try
block|{
name|endpointProxy
operator|.
name|beforeDelivery
argument_list|(
name|ActiveMQEndpointWorker
operator|.
name|ON_MESSAGE_METHOD
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|doOnMessageExpectInvalidMessageEndpointException
argument_list|()
expr_stmt|;
name|doAfterDeliveryExpectInvalidMessageEndpointException
argument_list|()
expr_stmt|;
name|doFullyDeadCheck
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOnMessageFailure
parameter_list|()
block|{
name|setupBeforeDeliverySuccessful
argument_list|()
expr_stmt|;
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"onMessage"
argument_list|)
operator|.
name|with
argument_list|(
name|same
argument_list|(
name|stubMessage
operator|.
name|proxy
argument_list|()
argument_list|)
argument_list|)
operator|.
name|will
argument_list|(
name|throwException
argument_list|(
operator|new
name|RuntimeException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setupAfterDeliverySuccessful
argument_list|()
expr_stmt|;
name|doBeforeDeliveryExpectSuccess
argument_list|()
expr_stmt|;
try|try
block|{
name|endpointProxy
operator|.
name|onMessage
argument_list|(
operator|(
name|Message
operator|)
name|stubMessage
operator|.
name|proxy
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|doAfterDeliveryExpectSuccess
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAfterDeliveryFailure
parameter_list|()
block|{
name|setupBeforeDeliverySuccessful
argument_list|()
expr_stmt|;
name|setupOnMessageSuccessful
argument_list|()
expr_stmt|;
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"afterDelivery"
argument_list|)
operator|.
name|will
argument_list|(
name|throwException
argument_list|(
operator|new
name|ResourceException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setupExpectRelease
argument_list|()
expr_stmt|;
name|doBeforeDeliveryExpectSuccess
argument_list|()
expr_stmt|;
name|doOnMessageExpectSuccess
argument_list|()
expr_stmt|;
try|try
block|{
name|endpointProxy
operator|.
name|afterDelivery
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"An exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|doFullyDeadCheck
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doFullyDeadCheck
parameter_list|()
block|{
name|doBeforeDeliveryExpectInvalidMessageEndpointException
argument_list|()
expr_stmt|;
name|doOnMessageExpectInvalidMessageEndpointException
argument_list|()
expr_stmt|;
name|doAfterDeliveryExpectInvalidMessageEndpointException
argument_list|()
expr_stmt|;
name|doReleaseExpectInvalidMessageEndpointException
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setupAfterDeliverySuccessful
parameter_list|()
block|{
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"afterDelivery"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupOnMessageSuccessful
parameter_list|()
block|{
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"onMessage"
argument_list|)
operator|.
name|with
argument_list|(
name|same
argument_list|(
name|stubMessage
operator|.
name|proxy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupBeforeDeliverySuccessful
parameter_list|()
block|{
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"beforeDelivery"
argument_list|)
operator|.
name|with
argument_list|(
name|isA
argument_list|(
name|Method
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupExpectRelease
parameter_list|()
block|{
name|mockEndpoint
operator|.
name|expects
argument_list|(
name|once
argument_list|()
argument_list|)
operator|.
name|method
argument_list|(
literal|"release"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doBeforeDeliveryExpectSuccess
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|beforeDelivery
argument_list|(
name|ActiveMQEndpointWorker
operator|.
name|ON_MESSAGE_METHOD
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"No exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doOnMessageExpectSuccess
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|onMessage
argument_list|(
operator|(
name|Message
operator|)
name|stubMessage
operator|.
name|proxy
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
name|fail
argument_list|(
literal|"No exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doAfterDeliveryExpectSuccess
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|afterDelivery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"No exception should have been thrown"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doBeforeDeliveryExpectInvalidMessageEndpointException
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|beforeDelivery
argument_list|(
name|ActiveMQEndpointWorker
operator|.
name|ON_MESSAGE_METHOD
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An InvalidMessageEndpointException should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidMessageEndpointException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"An InvalidMessageEndpointException should have been thrown"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doOnMessageExpectInvalidMessageEndpointException
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|onMessage
argument_list|(
operator|(
name|Message
operator|)
name|stubMessage
operator|.
name|proxy
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An InvalidMessageEndpointException should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidMessageEndpointException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doAfterDeliveryExpectInvalidMessageEndpointException
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|afterDelivery
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"An InvalidMessageEndpointException should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidMessageEndpointException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"An InvalidMessageEndpointException should have been thrown"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doReleaseExpectInvalidMessageEndpointException
parameter_list|()
block|{
try|try
block|{
name|endpointProxy
operator|.
name|release
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"An InvalidMessageEndpointException should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidMessageEndpointException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
interface|interface
name|EndpointAndListener
extends|extends
name|MessageListener
extends|,
name|MessageEndpoint
block|{     }
block|}
end_class

end_unit

