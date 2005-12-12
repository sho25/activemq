begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|oneport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|omg
operator|.
name|PortableInterceptor
operator|.
name|ORBInitInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openorb
operator|.
name|orb
operator|.
name|pi
operator|.
name|FeatureInitInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openorb
operator|.
name|orb
operator|.
name|pi
operator|.
name|FeatureInitializer
import|;
end_import

begin_comment
comment|/**  * Used to hook in the OpenORBOpenPortSocketFactory into the ORB.  */
end_comment

begin_class
specifier|public
class|class
name|OpenORBOpenPortFeatureInitializer
implements|implements
name|FeatureInitializer
block|{
specifier|static
specifier|final
specifier|private
name|ThreadLocal
name|socketFatory
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
specifier|static
specifier|public
name|void
name|setContextSocketFactory
parameter_list|(
name|OpenORBOpenPortSocketFactory
name|sf
parameter_list|)
block|{
name|socketFatory
operator|.
name|set
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|ORBInitInfo
name|orbinfo
parameter_list|,
name|FeatureInitInfo
name|featureinfo
parameter_list|)
block|{
name|OpenORBOpenPortSocketFactory
name|sf
init|=
operator|(
name|OpenORBOpenPortSocketFactory
operator|)
name|socketFatory
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
name|featureinfo
operator|.
name|setFeature
argument_list|(
literal|"IIOP.SocketFactory"
argument_list|,
name|sf
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

