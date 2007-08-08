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
name|jndi
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|CompositeName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_comment
comment|/**  * A default implementation of {@link NameParser}  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|NameParserImpl
implements|implements
name|NameParser
block|{
specifier|public
name|Name
name|parse
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|NamingException
block|{
return|return
operator|new
name|CompositeName
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

