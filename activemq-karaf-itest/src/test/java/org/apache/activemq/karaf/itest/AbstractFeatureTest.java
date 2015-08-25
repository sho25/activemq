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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|service
operator|.
name|command
operator|.
name|CommandProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|service
operator|.
name|command
operator|.
name|CommandSession
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
name|jaas
operator|.
name|boot
operator|.
name|principal
operator|.
name|RolePrincipal
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
name|jaas
operator|.
name|boot
operator|.
name|principal
operator|.
name|UserPrincipal
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
name|Session
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
name|karaf
operator|.
name|options
operator|.
name|KarafDistributionOption
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
name|UrlReference
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|ExecutorService
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
name|Executors
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
name|FutureTask
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
name|*
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFeatureTest
block|{
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
name|long
name|COMMAND_TIMEOUT
init|=
literal|30000L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"karaf"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"karaf"
decl_stmt|;
specifier|static
name|String
name|basedir
decl_stmt|;
static|static
block|{
try|try
block|{
name|File
name|location
init|=
operator|new
name|File
argument_list|(
name|AbstractFeatureTest
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|basedir
operator|=
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"../.."
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"basedir="
operator|+
name|basedir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
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
annotation|@
name|Inject
name|SessionFactory
name|sessionFactory
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|executeCommand
parameter_list|(
specifier|final
name|String
name|command
parameter_list|,
specifier|final
name|Long
name|timeout
parameter_list|,
specifier|final
name|Boolean
name|silent
parameter_list|)
block|{
name|String
name|response
decl_stmt|;
specifier|final
name|ByteArrayOutputStream
name|byteArrayOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|PrintStream
name|printStream
init|=
operator|new
name|PrintStream
argument_list|(
name|byteArrayOutputStream
argument_list|)
decl_stmt|;
specifier|final
name|Session
name|commandSession
init|=
name|sessionFactory
operator|.
name|create
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|printStream
argument_list|,
name|printStream
argument_list|)
decl_stmt|;
name|commandSession
operator|.
name|put
argument_list|(
literal|"APPLICATION"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"karaf.name"
argument_list|,
literal|"root"
argument_list|)
argument_list|)
expr_stmt|;
name|commandSession
operator|.
name|put
argument_list|(
literal|"USER"
argument_list|,
name|USER
argument_list|)
expr_stmt|;
name|FutureTask
argument_list|<
name|String
argument_list|>
name|commandFuture
init|=
operator|new
name|FutureTask
argument_list|<
name|String
argument_list|>
argument_list|(
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
literal|"admin"
argument_list|)
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|RolePrincipal
argument_list|(
literal|"admin"
argument_list|)
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|RolePrincipal
argument_list|(
literal|"manager"
argument_list|)
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|RolePrincipal
argument_list|(
literal|"viewer"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
operator|!
name|silent
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|commandSession
operator|.
name|execute
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
name|printStream
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|byteArrayOutputStream
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|executor
operator|.
name|submit
argument_list|(
name|commandFuture
argument_list|)
expr_stmt|;
name|response
operator|=
name|commandFuture
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|response
operator|=
literal|"SHELL COMMAND TIMED OUT: "
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Execute: "
operator|+
name|command
operator|+
literal|" - Response:"
operator|+
name|response
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
specifier|protected
name|String
name|executeCommand
parameter_list|(
specifier|final
name|String
name|command
parameter_list|)
block|{
return|return
name|executeCommand
argument_list|(
name|command
argument_list|,
name|COMMAND_TIMEOUT
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** 	 * Installs a feature and asserts that feature is properly installed. 	 * @param feature 	 * @throws Exception 	 */
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
name|executeCommand
argument_list|(
literal|"feature:list -i"
argument_list|)
expr_stmt|;
name|executeCommand
argument_list|(
literal|"feature:install "
operator|+
name|feature
argument_list|)
expr_stmt|;
name|assertFeatureInstalled
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
name|executeCommand
argument_list|(
literal|"feature:list -i"
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
name|boolean
name|verifyBundleInstalled
parameter_list|(
specifier|final
name|String
name|bundleName
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Checking: "
operator|+
name|bundle
operator|.
name|getSymbolicName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|bundle
operator|.
name|getSymbolicName
argument_list|()
operator|.
name|contains
argument_list|(
name|bundleName
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|found
return|;
block|}
specifier|public
specifier|static
name|String
name|karafVersion
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"karafVersion"
argument_list|,
literal|"unknown-need-env-var"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|UrlReference
name|getActiveMQKarafFeatureUrl
parameter_list|()
block|{
name|String
name|type
init|=
literal|"xml/features"
decl_stmt|;
name|UrlReference
name|urlReference
init|=
name|mavenBundle
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
name|type
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"FeatureURL: "
operator|+
name|urlReference
operator|.
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|urlReference
return|;
block|}
comment|// for use from a probe
specifier|public
name|String
name|getCamelFeatureUrl
parameter_list|()
block|{
return|return
name|getCamelFeatureUrl
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"camel.version"
argument_list|,
literal|"unknown"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getCamelFeatureUrl
parameter_list|(
name|String
name|ver
parameter_list|)
block|{
return|return
literal|"mvn:org.apache.camel.karaf/apache-camel/"
operator|+
name|ver
operator|+
literal|"/xml/features"
return|;
block|}
specifier|public
specifier|static
name|UrlReference
name|getKarafFeatureUrl
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"*** The karaf version is "
operator|+
name|karafVersion
argument_list|()
operator|+
literal|" ***"
argument_list|)
expr_stmt|;
name|String
name|type
init|=
literal|"xml/features"
decl_stmt|;
return|return
name|mavenBundle
argument_list|()
operator|.
name|groupId
argument_list|(
literal|"org.apache.karaf.assemblies.features"
argument_list|)
operator|.
name|artifactId
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|version
argument_list|(
name|karafVersion
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Option
index|[]
name|configureBrokerStart
parameter_list|(
name|Option
index|[]
name|existingOptions
parameter_list|,
name|String
name|xmlConfig
parameter_list|)
block|{
name|existingOptions
operator|=
name|append
argument_list|(
name|replaceConfigurationFile
argument_list|(
literal|"etc/activemq.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/karaf/itest/"
operator|+
name|xmlConfig
operator|+
literal|".xml"
argument_list|)
argument_list|)
argument_list|,
name|existingOptions
argument_list|)
expr_stmt|;
return|return
name|append
argument_list|(
name|replaceConfigurationFile
argument_list|(
literal|"etc/org.apache.activemq.server-default.cfg"
argument_list|,
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/karaf/itest/org.apache.activemq.server-default.cfg"
argument_list|)
argument_list|)
argument_list|,
name|existingOptions
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Option
index|[]
name|configureBrokerStart
parameter_list|(
name|Option
index|[]
name|existingOptions
parameter_list|)
block|{
specifier|final
name|String
name|xmlConfig
init|=
literal|"activemq"
decl_stmt|;
return|return
name|configureBrokerStart
argument_list|(
name|existingOptions
argument_list|,
name|xmlConfig
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Option
index|[]
name|append
parameter_list|(
name|Option
name|toAdd
parameter_list|,
name|Option
index|[]
name|existingOptions
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Option
argument_list|>
name|newOptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Option
argument_list|>
argument_list|()
decl_stmt|;
name|newOptions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|existingOptions
argument_list|)
argument_list|)
expr_stmt|;
name|newOptions
operator|.
name|add
argument_list|(
name|toAdd
argument_list|)
expr_stmt|;
return|return
name|newOptions
operator|.
name|toArray
argument_list|(
operator|new
name|Option
index|[]
block|{}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Option
index|[]
name|configure
parameter_list|(
name|String
modifier|...
name|features
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|f
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|f
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|features
argument_list|)
argument_list|)
expr_stmt|;
name|Option
index|[]
name|options
init|=
operator|new
name|Option
index|[]
block|{
name|karafDistributionConfiguration
argument_list|()
operator|.
name|frameworkUrl
argument_list|(
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
argument_list|)
operator|.
name|karafVersion
argument_list|(
name|karafVersion
argument_list|()
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
block|,
name|KarafDistributionOption
operator|.
name|keepRuntimeFolder
argument_list|()
block|,
name|logLevel
argument_list|(
name|LogLevelOption
operator|.
name|LogLevel
operator|.
name|WARN
argument_list|)
block|,
name|editConfigurationFilePut
argument_list|(
literal|"etc/config.properties"
argument_list|,
literal|"karaf.startlevel.bundle"
argument_list|,
literal|"50"
argument_list|)
block|,
comment|//debugConfiguration("5005", true),
name|features
argument_list|(
name|getActiveMQKarafFeatureUrl
argument_list|()
argument_list|,
name|f
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|f
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
block|}
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|contains
argument_list|(
literal|"activemq-camel"
argument_list|)
condition|)
block|{
name|options
operator|=
name|append
argument_list|(
name|features
argument_list|(
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
name|versionAsInProject
argument_list|()
operator|.
name|type
argument_list|(
literal|"xml/features"
argument_list|)
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
return|return
name|options
return|;
block|}
specifier|protected
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
name|Throwable
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
block|}
end_class

end_unit

