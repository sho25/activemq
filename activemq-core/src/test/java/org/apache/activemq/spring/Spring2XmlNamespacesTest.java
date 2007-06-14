begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
specifier|public
class|class
name|Spring2XmlNamespacesTest
extends|extends
name|SpringTestSupport
block|{
specifier|public
name|void
name|testUsingSpringXmlNamespacesWithPublicXsdLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSenderConfig
argument_list|(
literal|"spring-embedded-xbean.xml"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

