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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|PaxExam
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|karaf
operator|.
name|options
operator|.
name|KarafDistributionOption
operator|.
name|editConfigurationFilePut
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|karaf
operator|.
name|options
operator|.
name|KarafDistributionOption
operator|.
name|replaceConfigurationFile
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|PaxExam
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ActiveMQBrokerRuntimeConfigTest
extends|extends
name|AbstractJmsFeatureTest
block|{
annotation|@
name|Configuration
specifier|public
specifier|static
name|Option
index|[]
name|configure
parameter_list|()
block|{
return|return
name|append
argument_list|(
name|editConfigurationFilePut
argument_list|(
literal|"etc/org.apache.activemq.server-default.cfg"
argument_list|,
literal|"config.check"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
name|configureBrokerStart
argument_list|(
name|append
argument_list|(
name|replaceConfigurationFile
argument_list|(
literal|"data/tmp/modified-config.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/karaf/itest/activemq-runtime-config-mod.xml"
argument_list|)
argument_list|)
argument_list|,
name|configure
argument_list|(
literal|"activemq"
argument_list|)
argument_list|)
argument_list|,
literal|"activemq-runtime-config"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|2
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Throwable
block|{
name|withinReason
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"brokerName = amq-broker"
argument_list|,
name|executeCommand
argument_list|(
literal|"activemq:list"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|withinReason
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"3MB limit"
argument_list|,
name|executeCommand
argument_list|(
literal|"activemq:query"
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|contains
argument_list|(
literal|"MemoryLimit = 3145728"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// ensure update will be reflected in OS fs modified window
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// increase from 3mb to 4mb and check
name|String
name|karafDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"karaf.base"
argument_list|)
decl_stmt|;
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|karafDir
operator|+
literal|"/etc/activemq.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Modifying configuration at: "
operator|+
name|target
operator|+
literal|"last mod: "
operator|+
operator|new
name|Date
argument_list|(
name|target
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|karafDir
operator|+
literal|"/data/tmp/modified-config.xml"
argument_list|)
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"new mod at: "
operator|+
operator|new
name|Date
argument_list|(
name|target
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|withinReason
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"4MB limit"
argument_list|,
name|executeCommand
argument_list|(
literal|"activemq:query"
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|contains
argument_list|(
literal|"MemoryLimit = 4194304"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

