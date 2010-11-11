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
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|xbean
operator|.
name|XBeanBrokerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|// https://issues.apache.org/activemq/browse/AMQ-2939
end_comment

begin_class
specifier|public
class|class
name|OutOfOrderXMLTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|verifyBrokerCreationWhenXmlOutOfOrderValidationFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:org/apache/activemq/broker/out-of-order-broker-elements.xml?validate=false"
argument_list|)
argument_list|)
decl_stmt|;
name|answer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

