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
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|ConnectionFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|JUnit4TestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|options
operator|.
name|MavenArtifactProvisionOption
import|;
end_import

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
name|JMSException
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|JUnit4TestRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ActiveMQAMQPBrokerFeatureTest
extends|extends
name|ActiveMQBrokerFeatureTest
block|{
specifier|private
specifier|static
specifier|final
name|Integer
name|AMQP_PORT
init|=
literal|61636
decl_stmt|;
annotation|@
name|Configuration
specifier|public
specifier|static
name|Option
index|[]
name|configure
parameter_list|()
block|{
name|Option
index|[]
name|activeMQOptions
init|=
name|configure
argument_list|(
literal|"activemq"
argument_list|)
decl_stmt|;
name|MavenArtifactProvisionOption
name|qpidClient
init|=
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"org.apache.qpid"
argument_list|,
literal|"qpid-amqp-1-0-client"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
decl_stmt|;
name|MavenArtifactProvisionOption
name|qpidClientJms
init|=
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"org.apache.qpid"
argument_list|,
literal|"qpid-amqp-1-0-client-jms"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
decl_stmt|;
name|MavenArtifactProvisionOption
name|qpidCommon
init|=
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"org.apache.qpid"
argument_list|,
literal|"qpid-amqp-1-0-common"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
decl_stmt|;
name|MavenArtifactProvisionOption
name|geronimoJms
init|=
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"org.apache.geronimo.specs"
argument_list|,
literal|"geronimo-jms_1.1_spec"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
decl_stmt|;
name|MavenArtifactProvisionOption
name|geronimoJta
init|=
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"org.apache.geronimo.specs"
argument_list|,
literal|"geronimo-jta_1.1_spec"
argument_list|,
literal|"1.1.1"
argument_list|)
decl_stmt|;
name|Option
index|[]
name|options
init|=
name|append
argument_list|(
name|qpidClient
argument_list|,
name|activeMQOptions
argument_list|)
decl_stmt|;
name|options
operator|=
name|append
argument_list|(
name|qpidClientJms
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|options
operator|=
name|append
argument_list|(
name|qpidCommon
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|options
operator|=
name|append
argument_list|(
name|geronimoJms
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|options
operator|=
name|append
argument_list|(
name|geronimoJta
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Option
index|[]
name|configuredOptions
init|=
name|configureBrokerStart
argument_list|(
name|options
argument_list|)
decl_stmt|;
return|return
name|configuredOptions
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|ConnectionFactoryImpl
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|AMQP_PORT
argument_list|,
name|AbstractFeatureTest
operator|.
name|USER
argument_list|,
name|AbstractFeatureTest
operator|.
name|PASSWORD
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
end_class

end_unit

