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
name|spring
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
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
import|;
end_import

begin_class
specifier|public
class|class
name|SpringTest
extends|extends
name|SpringTestSupport
block|{
comment|/**      * Uses ActiveMQConnectionFactory to create the connection context.      * Configuration file is /resources/spring.xml      *      * @throws Exception      */
specifier|public
name|void
name|testSenderWithSpringXml
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|config
init|=
literal|"spring.xml"
decl_stmt|;
name|assertSenderConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Spring configured test that uses ActiveMQConnectionFactory for      * connection context and ActiveMQQueue for destination. Configuration      * file is /resources/spring-queue.xml.      *      * @throws Exception      */
specifier|public
name|void
name|testSenderWithSpringXmlAndQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|config
init|=
literal|"spring-queue.xml"
decl_stmt|;
name|assertSenderConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Spring configured test that uses JNDI. Configuration file is      * /resources/spring-jndi.xml.      *      * @throws Exception      */
specifier|public
name|void
name|testSenderWithSpringXmlUsingJNDI
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|config
init|=
literal|"spring-jndi.xml"
decl_stmt|;
name|assertSenderConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Spring configured test where in the connection context is set to use      * an embedded broker. Configuration file is /resources/spring-embedded.xml      * and /resources/activemq.xml.      *      * @throws Exception      */
specifier|public
name|void
name|testSenderWithSpringXmlEmbeddedBrokerConfiguredViaXml
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|config
init|=
literal|"spring-embedded.xml"
decl_stmt|;
name|assertSenderConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Spring configured test case that tests the remotely deployed xsd      * http://people.apache.org/repository/org.apache.activemq/xsds/activemq-core-4.1-SNAPSHOT.xsd      *      * @throws Exception      */
specifier|public
name|void
name|testSenderWithSpringXmlUsingSpring2NamespacesWithEmbeddedBrokerConfiguredViaXml
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|config
init|=
literal|"spring-embedded-xbean.xml"
decl_stmt|;
name|assertSenderConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Spring configured test case that tests the locally generated xsd      *      * @throws Exception      */
specifier|public
name|void
name|testSenderWithSpringXmlUsingSpring2NamespacesWithEmbeddedBrokerConfiguredViaXmlUsingLocalXsd
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|config
init|=
literal|"spring-embedded-xbean-local.xml"
decl_stmt|;
name|assertSenderConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

