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
name|util
package|;
end_package

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|IOHelper
block|{
specifier|private
name|IOHelper
parameter_list|()
block|{     }
specifier|public
specifier|static
name|String
name|getDefaultDataDirectory
parameter_list|()
block|{
return|return
name|getDefaultDirectoryPrefix
argument_list|()
operator|+
literal|"activemq-data"
return|;
block|}
specifier|public
specifier|static
name|String
name|getDefaultStoreDirectory
parameter_list|()
block|{
return|return
name|getDefaultDirectoryPrefix
argument_list|()
operator|+
literal|"amqstore"
return|;
block|}
comment|/**      * Allows a system property to be used to overload the default data      * directory which can be useful for forcing the test cases to use a target/      * prefix      */
specifier|public
specifier|static
name|String
name|getDefaultDirectoryPrefix
parameter_list|()
block|{
try|try
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activemq.default.directory.prefix"
argument_list|,
literal|""
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
block|}
end_class

end_unit

