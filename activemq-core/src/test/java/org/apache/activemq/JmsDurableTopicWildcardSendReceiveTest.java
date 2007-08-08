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
name|DeliveryMode
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
name|test
operator|.
name|JmsTopicSendReceiveTest
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsDurableTopicWildcardSendReceiveTest
extends|extends
name|JmsTopicSendReceiveTest
block|{
comment|/**      * Sets up a test with a topic destination, durable suscriber and persistent delivery mode.       *       * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|topic
operator|=
literal|true
expr_stmt|;
name|durable
operator|=
literal|true
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|PERSISTENT
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the consumer subject.       */
specifier|protected
name|String
name|getConsumerSubject
parameter_list|()
block|{
return|return
literal|"FOO.>"
return|;
block|}
comment|/**      * Returns the producer subject.       */
specifier|protected
name|String
name|getProducerSubject
parameter_list|()
block|{
return|return
literal|"FOO.BAR.HUMBUG"
return|;
block|}
block|}
end_class

end_unit

