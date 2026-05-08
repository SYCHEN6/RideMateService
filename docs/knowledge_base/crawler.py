import requests
from bs4 import BeautifulSoup
import os
import time
import re
from urllib.parse import urljoin, urlparse

# 配置
BASE_URL = 'https://www.bicycling.com/'
CRAWLER_DELAY = 2  # 爬虫延迟，避免请求过快
MAX_DEPTH = 3  # 最大爬取深度
OUTPUT_DIR = './'

topic_urls = {
    "cycling_equipment": "https://www.bicycling.com/bikes-gear/a20004998/best-bike-gear/",
    "cycling_safety": "https://www.bicycling.com/health-safety/a20003020/cycling-safety-tips/",
    "cycling_maintenance": "https://www.bicycling.com/maintenance/a20002457/bike-maintenance-tips/",
    "cycling_training": "https://www.bicycling.com/training/a20002245/cycling-training-tips/"
}

# 确保输出目录存在
os.makedirs(OUTPUT_DIR, exist_ok=True)

# 已访问的URL集合，避免重复爬取
visited_urls = set()

# 提取网页内容的函数
def extract_content(soup, url):
    # 提取标题
    title = soup.find('h1')
    title_text = title.text.strip() if title else "Untitled"
    
    # 提取正文内容
    # 根据不同网站的结构调整选择器
    content_div = soup.find('div', class_='article-body') or soup.find('div', class_='content') or soup.find('main')
    
    if not content_div:
        # 如果找不到正文div，尝试找到所有的p标签
        paragraphs = soup.find_all('p')
        content = "\n".join([p.text.strip() for p in paragraphs])
    else:
        # 清理内容，移除脚本、样式和广告
        for script in content_div(['script', 'style', 'aside', 'nav', 'footer']):
            script.decompose()
        
        # 提取所有段落
        paragraphs = content_div.find_all('p')
        content = "\n".join([p.text.strip() for p in paragraphs])
    
    # 清理内容
    content = re.sub(r'\s+', ' ', content)
    content = content.strip()
    
    return {
        "title": title_text,
        "url": url,
        "content": content
    }

# 保存内容到markdown文件
def save_to_markdown(content, topic):
    # 生成安全的文件名
    filename = re.sub(r'[^a-zA-Z0-9\-_]', '_', content['title'].lower())[:50] + '.md'
    
    # 创建主题子目录
    topic_dir = os.path.join(OUTPUT_DIR, topic)
    os.makedirs(topic_dir, exist_ok=True)
    
    # 完整文件路径
    file_path = os.path.join(topic_dir, filename)
    
    # 写入文件
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(f"# {content['title']}\n\n")
        f.write(f"**来源**: [{content['url']}]({content['url']})\n\n")
        f.write(content['content'])
    
    print(f"已保存: {file_path}")

# 爬虫主函数
def crawl(url, topic, depth=0):
    if depth > MAX_DEPTH:
        return
    
    if url in visited_urls:
        return
    
    print(f"\n爬取: {url}")
    print(f"深度: {depth}, 主题: {topic}")
    
    try:
        # 发送HTTP请求
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()  # 检查请求是否成功
        
        # 解析HTML
        soup = BeautifulSoup(response.content, 'html.parser')
        
        # 提取并保存内容
        content = extract_content(soup, url)
        if content['content'] and len(content['content']) > 500:  # 只保存有足够内容的页面
            save_to_markdown(content, topic)
        
        # 标记为已访问
        visited_urls.add(url)
        
        # 查找相关链接并继续爬取
        if depth < MAX_DEPTH:
            links = soup.find_all('a', href=True)
            print(f"找到 {len(links)} 个链接")
            
            for link in links:
                href = link['href']
                full_url = urljoin(url, href)
                
                # 确保链接属于同一网站
                if urlparse(full_url).netloc == urlparse(url).netloc:
                    # 确保链接与当前主题相关
                    if any(keyword in full_url.lower() for keyword in [topic.replace('_', '-'), 'bike', 'cycling']):
                        # 避免爬取无关页面
                        if not any(exclude in full_url.lower() for exclude in ['login', 'register', 'advertise', 'contact', 'privacy', 'terms']):
                            time.sleep(CRAWLER_DELAY)
                            crawl(full_url, topic, depth + 1)
    
    except Exception as e:
        print(f"爬取失败: {url}")
        print(f"错误: {str(e)}")

# 主程序
if __name__ == "__main__":
    print("开始爬取骑行知识...")
    
    for topic, url in topic_urls.items():
        print(f"\n=== 开始爬取主题: {topic} ===")
        crawl(url, topic)
    
    print(f"\n爬取完成！")
    print(f"已爬取 {len(visited_urls)} 个网页")
    print(f"内容已保存到: {OUTPUT_DIR}")