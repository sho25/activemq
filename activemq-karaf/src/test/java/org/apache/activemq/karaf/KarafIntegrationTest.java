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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|testing
operator|.
name|AbstractIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|testing
operator|.
name|Helper
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
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|*
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
name|OptionUtils
operator|.
name|combine
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
name|container
operator|.
name|def
operator|.
name|PaxRunnerOptions
operator|.
name|scanFeatures
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
name|container
operator|.
name|def
operator|.
name|PaxRunnerOptions
operator|.
name|workingDirectory
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
name|KarafIntegrationTest
extends|extends
name|AbstractIntegrationTest
block|{
annotation|@
name|Configuration
specifier|public
specifier|static
name|Option
index|[]
name|configuration
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|combine
argument_list|(
comment|// Default karaf environment
name|Helper
operator|.
name|getDefaultOptions
argument_list|(
name|systemProperty
argument_list|(
literal|"org.ops4j.pax.logging.DefaultServiceLog.level"
argument_list|)
operator|.
name|value
argument_list|(
literal|"DEBUG"
argument_list|)
argument_list|)
argument_list|,
name|scanFeatures
argument_list|(
name|maven
argument_list|()
operator|.
name|groupId
argument_list|(
literal|"org.apache.activemq"
argument_list|)
operator|.
name|artifactId
argument_list|(
literal|"activemq-karaf"
argument_list|)
operator|.
name|type
argument_list|(
literal|"xml"
argument_list|)
operator|.
name|classifier
argument_list|(
literal|"features"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
argument_list|,
literal|"activemq"
argument_list|)
argument_list|,
name|workingDirectory
argument_list|(
literal|"target/paxrunner/features/"
argument_list|)
argument_list|,
name|waitForFrameworkStartup
argument_list|()
argument_list|,
comment|// Test on both equinox and felix
name|equinox
argument_list|()
argument_list|,
name|felix
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFeatures
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Run some commands to make sure they are installed properly
comment|//        CommandProcessor cp = getOsgiService(CommandProcessor.class);
comment|//        CommandSession cs = cp.createSession(System.in, System.out, System.err);
comment|//        Object res = cs.execute("osgi:list");
comment|//        System.out.println(res);
comment|//        cs.close();
block|}
block|}
end_class

end_unit

