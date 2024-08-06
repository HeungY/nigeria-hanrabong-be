import requests
from bs4 import BeautifulSoup
import re
import json
import redis

# Step 1: Make an HTTP request to the website
url = 'https://m.badatime.com/view_point.jsp?idx=67'
response = requests.get(url)

# Check if the request was successful
if response.status_code == 200:
    # Step 2: Parse the HTML content
    soup = BeautifulSoup(response.text, 'html.parser')

    # Step 3: Find all <script> tags
    script_tags = soup.find_all('script')

    # Step 4: Extract the script content containing "var positions"
    positions_script = None
    for script in script_tags:
        if 'var positions' in script.text:
            positions_script = script.text
            break

    # Extract the substring from 'var positions' to the end of the array
    start = positions_script.find('var positions') - 1
    end = positions_script.find(']', start) + 1
    positions_string = positions_script[start:end]

    # Clean up the string to make it a valid JSON format
    positions_string = re.sub(r'var positions =', '', positions_string)
    positions_string = re.sub(r'new daum.maps.LatLng\(([^)]+)\)', r'{\1}', positions_string)
    positions_string = positions_string.strip().rstrip(';')

    # Convert single quotes to double quotes
    positions_string = positions_string.replace("'", '"')

    # Remove the content field
    positions_string = re.sub(r'content: ".*?",\s*', '', positions_string)

    # Remove unnecessary whitespace
    positions_string = re.sub(r'\s+', ' ', positions_string).strip()

    # Ensure all keys and string values are enclosed in double quotes
    positions_string = re.sub(r'(\w+):', r'"\1":', positions_string)
    positions_string = re.sub(r': (\w+)', r': "\1"', positions_string)

    # Fix the latlng objects to be in JSON format
    positions_string = re.sub(r'"latlng": \{([^,]+), ([^}]+)\}', r'"latlng": {"lat": \1, "lng": \2}', positions_string)

    # Add the opening and closing square brackets if they are missing
    positions_string = positions_string.strip('"')
    positions_string = re.sub(r',\s*([\]}])', r'\1', positions_string)


    # Function to simplify the target field
    def simplify_targets(match):
        targets = match.group(1).split('<br>')
        simplified_targets = set()
        for target in targets:
            species = target.split('-')[0]
            simplified_targets.add(species)
        return '"target": "' + ', '.join(simplified_targets) + '"'


    # Simplify the target field
    positions_string = re.sub(r'"target": "(.*?)"', simplify_targets, positions_string)

    # Load the JSON data
    positions_json = json.loads(positions_string)

    # 한림읍: 한림항 외측 테트라포트 97
    # 애월읍: 애월항 방파제 96
    # 제주시: 제주항방파제 빨간등대 95
    # 조천읍: 함덕서우봉해변앞해상 95
    # 구좌읍: 한동방파제 94
    # 성산읍: 성산포항 외 방파제 94
    # 표선면: 표선항 방파제 93
    # 남원읍: 남원포구 방파제 90
    # 서귀포: 서귀포항 방파제 91
    # 중문: 퍼시픽랜드 마리나항 방파제 91
    # 안덕면: 논짓물 주변 91

    # 임시 데이터
    temp_data = {
            "한림읍": ["한림항 외측 테트라포트", 97],
            "애월읍": ["애월항 방파제", 96],
            "제주시": ["제주항방파제 빨간등대", 95],
            "조천읍": ["함덕서우봉해변앞해상", 95],
            "구좌읍": ["한동방파제", 94],
            "성산읍": ["성산포항 외 방파제", 94],
            "표선면": ["표선항 방파제", 93],
            "남원읍": ["남원포구 방파제", 98],
            "서귀포": ["서귀포항 방파제", 91],
            "중문": ["퍼시픽랜드 마리나항 방파제", 91],
            "안덕면": ["논짓물 주변", 91]
    }

    # 최종 결과를 저장할 딕셔너리
    temp_final_result = {}

    # temp_data의 각 항목에 대해 처리
    for location, (description, button_id) in temp_data.items():
        # 요청할 URL
        url = f"https://www.badatime.com/fishing_point_ajax.jsp?tn={button_id}"

        try:
            # GET 요청 보내기
            response = requests.get(url)

            # 요청이 성공했는지 확인
            if response.status_code == 200:
                # 응답 데이터 가져오기
                data = response.text

                # 데이터 파싱 (예제에서는 '|'로 나눠진 문자열을 사용)
                saData = data.split("|")

                # 필요한 정보 추출
                info = {
                    '봄온': saData[4],
                    '봄물': saData[5],
                    '여름온': saData[6],
                    '여름물': saData[7],
                    '가을온': saData[8],
                    '가을물': saData[9],
                    '겨울온': saData[10],
                    '겨울물': saData[11]
                }

                # 저층 온도만 추출한 새로운 딕셔너리 생성
                extracted_data = {key: float(value.split('저층 : ')[1].replace('℃', '')) if '온' in key else value for key, value in info.items()}

                # 결과 저장
                temp_final_result[location] = extracted_data

        except Exception as e:
            print(f"Error processing {location}: {e}")


    # 'Location' 값과 일치하는 데이터를 필터링하고 'loc' 키를 추가하는 함수
    def filter_data_by_location(data, locations):
        filtered_data = []
        for loc_key, loc_value in locations.items():
            for item in data:
                if item['name'] == loc_value[0]:
                    new_item = {
                        'name': item['name'],
                        'latlng': item['latlng'],
                        'target': item['target'],
                        'loc': loc_key
                    }
                    filtered_data.append(new_item)
        return filtered_data

    r = redis.Redis(host='localhost', port=6379, db=0)

    # 필터링된 결과
    filtered_data = filter_data_by_location(positions_json, temp_data)

    # filtered_data와 temp_final_result를 합치는 함수
    def merge_data(filtered, temp_result):
        merged_result = {}
        for item in filtered:
            loc = item['loc']
            if loc in temp_result:
                merged_result[loc] = {**item, **temp_result[loc]}
        return merged_result

    # 합친 결과
    final_merged_result = merge_data(filtered_data, temp_final_result)

    # # Redis에 데이터 저장
    for key, value in final_merged_result.items():
        r.set(key, json.dumps(value, ensure_ascii=False))

    print(final_merged_result)
    print("Data has been stored in Redis.")
