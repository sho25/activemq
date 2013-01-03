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
name|openengsb
operator|.
name|labs
operator|.
name|paxexam
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
name|BundleContext
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
import|import static
name|org
operator|.
name|openengsb
operator|.
name|labs
operator|.
name|paxexam
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
name|openengsb
operator|.
name|labs
operator|.
name|paxexam
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFeatureTest
block|{
specifier|private
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
specifier|protected
name|BundleContext
name|bundleContext
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
comment|//    protected void testComponent(String component) throws Exception {
comment|//        long max = System.currentTimeMillis() + 10000;
comment|//        while (true) {
comment|//            try {
comment|//                assertNotNull("Cannot get component with name: " + component, createCamelContext().getComponent(component));
comment|//                return;
comment|//            } catch (Exception t) {
comment|//                if (System.currentTimeMillis()< max) {
comment|//                    Thread.sleep(1000);
comment|//                } else {
comment|//                    throw t;
comment|//                }
comment|//            }
comment|//        }
comment|//    }
comment|//
comment|//    protected void testDataFormat(String format) throws Exception {
comment|//        long max = System.currentTimeMillis() + 10000;
comment|//        while (true) {
comment|//            try {
comment|//                DataFormatDefinition dataFormatDefinition = createDataformatDefinition(format);
comment|//                assertNotNull(dataFormatDefinition);
comment|//                assertNotNull(dataFormatDefinition.getDataFormat(new DefaultRouteContext(createCamelContext())));
comment|//                return;
comment|//            } catch (Exception t) {
comment|//                if (System.currentTimeMillis()< max) {
comment|//                    Thread.sleep(1000);
comment|//                    continue;
comment|//                } else {
comment|//                    throw t;
comment|//                }
comment|//            }
comment|//        }
comment|//    }
comment|//
comment|//    protected DataFormatDefinition createDataformatDefinition(String format) {
comment|//        return null;
comment|//    }
comment|//    protected void testLanguage(String lang) throws Exception {
comment|//        long max = System.currentTimeMillis() + 10000;
comment|//        while (true) {
comment|//            try {
comment|//                assertNotNull(createCamelContext().resolveLanguage(lang));
comment|//                return;
comment|//            } catch (Exception t) {
comment|//                if (System.currentTimeMillis()< max) {
comment|//                    Thread.sleep(1000);
comment|//                    continue;
comment|//                } else {
comment|//                    throw t;
comment|//                }
comment|//            }
comment|//        }
comment|//    }
comment|//    protected CamelContext createCamelContext() throws Exception {
comment|//        CamelContextFactory factory = new CamelContextFactory();
comment|//        factory.setBundleContext(bundleContext);
comment|//        LOG.info("Get the bundleContext is " + bundleContext);
comment|//        return factory.createContext();
comment|//    }
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
literal|"2.3.0"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|activemqVersion
parameter_list|()
block|{
name|Package
name|p
init|=
name|Package
operator|.
name|getPackage
argument_list|(
literal|"org.apache.activemq"
argument_list|)
decl_stmt|;
name|String
name|version
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|version
operator|=
name|p
operator|.
name|getImplementationVersion
argument_list|()
expr_stmt|;
block|}
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemqVersion"
argument_list|,
name|version
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
return|return
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
name|version
argument_list|(
name|activemqVersion
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
comment|// install the cxf jaxb spec as the karaf doesn't provide it by default
comment|// f.add("cxf-jaxb");
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
name|version
argument_list|(
name|karafVersion
argument_list|()
argument_list|)
argument_list|)
comment|//This version doesn't affect the version of karaf we use
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
comment|// override the config.properties (to fix pax-exam bug)
name|replaceConfigurationFile
argument_list|(
literal|"etc/config.properties"
argument_list|,
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/karaf/itest/config.properties"
argument_list|)
argument_list|)
block|,
name|replaceConfigurationFile
argument_list|(
literal|"etc/custom.properties"
argument_list|,
operator|new
name|File
argument_list|(
name|basedir
operator|+
literal|"/src/test/resources/org/apache/activemq/karaf/itest/custom.properties"
argument_list|)
argument_list|)
block|,
name|scanFeatures
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
return|return
name|options
return|;
block|}
block|}
end_class

end_unit

