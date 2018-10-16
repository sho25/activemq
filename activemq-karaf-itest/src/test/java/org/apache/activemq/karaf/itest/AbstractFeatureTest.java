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
name|CoreOptions
operator|.
name|composite
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
name|maven
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
name|features
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
name|karafDistributionConfiguration
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
name|keepRuntimeFolder
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
name|logLevel
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
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|javax
operator|.
name|inject
operator|.
name|Inject
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
name|features
operator|.
name|FeaturesService
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
name|shell
operator|.
name|api
operator|.
name|console
operator|.
name|SessionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|MavenUtils
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
name|ProbeBuilder
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
name|TestProbeBuilder
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
import|import
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
name|LogLevelOption
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
name|MavenUrlReference
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
name|UrlReference
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
name|spi
operator|.
name|reactors
operator|.
name|ExamReactorStrategy
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
name|spi
operator|.
name|reactors
operator|.
name|PerClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Bundle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
annotation|@
name|ExamReactorStrategy
argument_list|(
name|PerClass
operator|.
name|class
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractFeatureTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|KARAF_MAJOR_VERSION
init|=
literal|"4.2.1"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractFeatureTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|ASSERTION_TIMEOUT
init|=
literal|30000L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_BASE
init|=
literal|"src/test/resources/org/apache/activemq/karaf/itest/"
decl_stmt|;
annotation|@
name|Inject
name|BundleContext
name|bundleContext
decl_stmt|;
annotation|@
name|Inject
name|FeaturesService
name|featuresService
decl_stmt|;
annotation|@
name|Inject
name|SessionFactory
name|sessionFactory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|ProbeBuilder
specifier|public
name|TestProbeBuilder
name|probeConfiguration
parameter_list|(
name|TestProbeBuilder
name|probe
parameter_list|)
block|{
name|probe
operator|.
name|setHeader
argument_list|(
name|Constants
operator|.
name|DYNAMICIMPORT_PACKAGE
argument_list|,
literal|"*,org.ops4j.pax.exam.options.*,org.apache.felix.service.*;status=provisional"
argument_list|)
expr_stmt|;
return|return
name|probe
return|;
block|}
comment|/**      * Installs a feature and asserts that feature is properly installed.      *       * @param feature      * @throws Exception      */
specifier|public
name|void
name|installAndAssertFeature
parameter_list|(
specifier|final
name|String
name|feature
parameter_list|)
throws|throws
name|Throwable
block|{
name|featuresService
operator|.
name|installFeature
argument_list|(
name|feature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertFeatureInstalled
parameter_list|(
specifier|final
name|String
name|feature
parameter_list|)
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
name|assertTrue
argument_list|(
literal|"Expected "
operator|+
name|feature
operator|+
literal|" feature to be installed."
argument_list|,
name|featuresService
operator|.
name|isInstalled
argument_list|(
name|featuresService
operator|.
name|getFeature
argument_list|(
name|feature
argument_list|)
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
specifier|public
name|Bundle
name|getBundle
parameter_list|(
name|String
name|symName
parameter_list|)
block|{
for|for
control|(
name|Bundle
name|bundle
range|:
name|bundleContext
operator|.
name|getBundles
argument_list|()
control|)
block|{
if|if
condition|(
name|bundle
operator|.
name|getSymbolicName
argument_list|()
operator|.
name|contains
argument_list|(
name|symName
argument_list|)
condition|)
block|{
return|return
name|bundle
return|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bundle "
operator|+
name|symName
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
specifier|protected
name|String
name|executeCommand
parameter_list|(
name|String
name|command
parameter_list|)
block|{
return|return
name|KarafShellHelper
operator|.
name|executeCommand
argument_list|(
name|sessionFactory
argument_list|,
name|command
argument_list|)
return|;
block|}
specifier|protected
name|void
name|assertBrokerStarted
parameter_list|()
throws|throws
name|Exception
block|{
name|withinReason
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
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
name|assertTrue
argument_list|(
name|executeCommand
argument_list|(
literal|"activemq:bstat"
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|contains
argument_list|(
literal|"BrokerName = amq-broker"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Option
name|configureBrokerStart
parameter_list|(
name|String
name|xmlConfig
parameter_list|)
block|{
return|return
name|composite
argument_list|(
name|replaceConfigurationFile
argument_list|(
literal|"etc/activemq.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|RESOURCE_BASE
operator|+
name|xmlConfig
operator|+
literal|".xml"
argument_list|)
argument_list|)
argument_list|,
name|replaceConfigurationFile
argument_list|(
literal|"etc/org.apache.activemq.server-default.cfg"
argument_list|,
operator|new
name|File
argument_list|(
name|RESOURCE_BASE
operator|+
literal|"org.apache.activemq.server-default.cfg"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Option
name|configureBrokerStart
parameter_list|()
block|{
return|return
name|configureBrokerStart
argument_list|(
literal|"activemq"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Option
name|configure
parameter_list|(
name|String
modifier|...
name|features
parameter_list|)
block|{
name|MavenUrlReference
name|karafUrl
init|=
name|maven
argument_list|()
operator|.
name|groupId
argument_list|(
literal|"org.apache.karaf"
argument_list|)
operator|.
name|artifactId
argument_list|(
literal|"apache-karaf"
argument_list|)
operator|.
name|type
argument_list|(
literal|"tar.gz"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
decl_stmt|;
name|UrlReference
name|camelUrl
init|=
name|maven
argument_list|()
operator|.
name|groupId
argument_list|(
literal|"org.apache.camel.karaf"
argument_list|)
operator|.
name|artifactId
argument_list|(
literal|"apache-camel"
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
decl_stmt|;
name|UrlReference
name|activeMQUrl
init|=
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
name|versionAsInProject
argument_list|()
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
decl_stmt|;
return|return
name|composite
argument_list|(
name|karafDistributionConfiguration
argument_list|()
operator|.
name|frameworkUrl
argument_list|(
name|karafUrl
argument_list|)
operator|.
name|karafVersion
argument_list|(
name|KARAF_MAJOR_VERSION
argument_list|)
operator|.
name|name
argument_list|(
literal|"Apache Karaf"
argument_list|)
operator|.
name|unpackDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/paxexam/unpack/"
argument_list|)
argument_list|)
argument_list|,
name|keepRuntimeFolder
argument_list|()
argument_list|,
comment|//
name|logLevel
argument_list|(
name|LogLevelOption
operator|.
name|LogLevel
operator|.
name|INFO
argument_list|)
argument_list|,
comment|//
name|editConfigurationFilePut
argument_list|(
literal|"etc/config.properties"
argument_list|,
literal|"karaf.startlevel.bundle"
argument_list|,
literal|"50"
argument_list|)
argument_list|,
comment|// debugConfiguration("5005", true),
name|features
argument_list|(
name|activeMQUrl
argument_list|,
name|features
argument_list|)
argument_list|,
comment|//
name|features
argument_list|(
name|camelUrl
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|String
name|camelVersion
parameter_list|()
block|{
return|return
name|MavenUtils
operator|.
name|getArtifactVersion
argument_list|(
literal|"org.apache.camel.karaf"
argument_list|,
literal|"apache-camel"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|withinReason
parameter_list|(
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|callable
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|max
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|ASSERTION_TIMEOUT
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|callable
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|max
condition|)
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
throw|throw
name|t
throw|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|withinReason
parameter_list|(
name|Runnable
name|runable
parameter_list|)
block|{
name|long
name|max
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|ASSERTION_TIMEOUT
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|runable
operator|.
name|run
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|max
condition|)
block|{
try|try
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
continue|continue;
block|}
else|else
block|{
throw|throw
name|t
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
specifier|public
specifier|static
name|void
name|copyFile
parameter_list|(
name|File
name|from
parameter_list|,
name|File
name|to
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|to
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Creating new file for: "
operator|+
name|to
argument_list|)
expr_stmt|;
name|to
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
name|FileChannel
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|from
argument_list|)
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|FileChannel
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|to
argument_list|)
operator|.
name|getChannel
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|size
init|=
name|in
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|position
operator|<
name|size
condition|)
block|{
name|position
operator|+=
name|in
operator|.
name|transferTo
argument_list|(
name|position
argument_list|,
literal|8192
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
end_class

end_unit

