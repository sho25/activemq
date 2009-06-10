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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_class
specifier|public
class|class
name|ReflectionSupportTest
extends|extends
name|TestCase
block|{
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|favorites
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|favoritesString
init|=
literal|"[queue://test, topic://test]"
decl_stmt|;
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|nonFavorites
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|nonFavoritesString
init|=
literal|"[topic://test1]"
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|favorites
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|favorites
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|nonFavorites
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetProperties
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|SimplePojo
name|pojo
init|=
operator|new
name|SimplePojo
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"age"
argument_list|,
literal|"27"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"Hiram"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"enabled"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"uri"
argument_list|,
literal|"test://value"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"favorites"
argument_list|,
name|favoritesString
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"nonFavorites"
argument_list|,
name|nonFavoritesString
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"others"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|pojo
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|27
argument_list|,
name|pojo
operator|.
name|getAge
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hiram"
argument_list|,
name|pojo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|pojo
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test://value"
argument_list|)
argument_list|,
name|pojo
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|favorites
argument_list|,
name|pojo
operator|.
name|getFavorites
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nonFavorites
argument_list|,
name|pojo
operator|.
name|getNonFavorites
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pojo
operator|.
name|getOthers
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetProperties
parameter_list|()
block|{
name|SimplePojo
name|pojo
init|=
operator|new
name|SimplePojo
argument_list|()
decl_stmt|;
name|pojo
operator|.
name|setAge
argument_list|(
literal|31
argument_list|)
expr_stmt|;
name|pojo
operator|.
name|setName
argument_list|(
literal|"Dejan"
argument_list|)
expr_stmt|;
name|pojo
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pojo
operator|.
name|setFavorites
argument_list|(
name|favorites
argument_list|)
expr_stmt|;
name|pojo
operator|.
name|setNonFavorites
argument_list|(
name|nonFavorites
argument_list|)
expr_stmt|;
name|pojo
operator|.
name|setOthers
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|getProperties
argument_list|(
name|pojo
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Dejan"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"31"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"age"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"enabled"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|favoritesString
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"favorites"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nonFavoritesString
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"nonFavorites"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"others"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetBoolean
parameter_list|()
block|{
name|TestWitBoolean
name|target
init|=
operator|new
name|TestWitBoolean
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|target
operator|.
name|getKeepAlive
argument_list|()
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperty
argument_list|(
name|target
argument_list|,
literal|"keepAlive"
argument_list|,
literal|"TRUE"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|getKeepAlive
argument_list|()
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperty
argument_list|(
name|target
argument_list|,
literal|"keepAlive"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|target
operator|.
name|getKeepAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|TestWitBoolean
block|{
specifier|private
name|Boolean
name|keepAlive
init|=
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|Boolean
name|getKeepAlive
parameter_list|()
block|{
return|return
name|keepAlive
return|;
block|}
specifier|public
name|void
name|setKeepAlive
parameter_list|(
name|Boolean
name|keepAlive
parameter_list|)
block|{
name|this
operator|.
name|keepAlive
operator|=
name|keepAlive
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

